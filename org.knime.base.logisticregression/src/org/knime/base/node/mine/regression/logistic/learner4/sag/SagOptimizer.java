/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ---------------------------------------------------------------------
 *
 * History
 *   09.03.2017 (Adrian): created
 */
package org.knime.base.node.mine.regression.logistic.learner4.sag;

import java.util.Iterator;

import org.knime.base.node.mine.regression.logistic.learner4.glmnet.ClassificationTrainingData;
import org.knime.base.node.mine.regression.logistic.learner4.glmnet.ClassificationTrainingRow;

/**
 * Optimizer based on the stochastic average gradient method.
 *
 * @author Adrian Nembach, KNIME.com
 */
public class SagOptimizer {

    public double[][] optimize(final ClassificationTrainingData data, final Loss<ClassificationTrainingRow> loss, final int maxIter, final double alpha, final double lambda) {
        final int nRows = data.getRowCount();
        final int nFets = data.getFeatureCount();
        final int nCats = data.getCategoryCount();
        // initialize
        double[][] g = new double[nCats - 1][nRows];
        double[][] d = new double[nCats - 1][nFets];
        int nCovered = 0;

        WeightVector w = new SimpleWeightVector(nFets, nCats);

        // iterate over samples
        Iterator<ClassificationTrainingRow> iterator = data.iterator();
        for (int k = 0; k < maxIter; k++) {
            ClassificationTrainingRow row;
            if (iterator.hasNext()) {
                row = iterator.next();
            } else {
                data.permute();
                iterator = data.iterator();
                row = iterator.next();
            }

            double[] sig = loss.gradient(row, w.predict(row));

            int id = row.getId();
            for (int c = 0; c < nCats - 1; c++) {
                // TODO exploit sparseness
                for (int i = 0; i < nFets; i++) {
                    d[c][i] += row.getFeature(i) * (sig[c] - g[c][id]);
                }
                g[c][id] = sig[c];
            }


            if (nCovered < nRows) {
                nCovered++;
            }

            w.scale(alpha, lambda);

            w.update(alpha, d, nCovered);



        }

        // finalize
        w.finalize(d);

        return w.getWeightVector();
    }


    private interface WeightVector {

        public void scale(double alpha, double lambda);

        public void update(double alpha, double[][] d, int nCovered);

        public void checkNormalize();

        public void finalize(final double[][] d);

        public double[][] getWeightVector();

        public double[] predict(final ClassificationTrainingRow row);
    }

    private interface WeightVectorConsumer {
        public double calculate(double val, int c, int i);
    }
    private abstract class AbstractWeightVector implements WeightVector {
        private final double[][] m_data;

        public AbstractWeightVector(final int nFets, final int nCats) {
            m_data = new double[nCats - 1][nFets];
        }


        protected void updateData(final WeightVectorConsumer func) {
            for (int c = 0; c < m_data.length; c++) {
                for (int i = 0; i < m_data[c].length; i++) {
                    m_data[c][i] = func.calculate(m_data[c][i], c, i);
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public double[][] getWeightVector() {
            return m_data;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public double[] predict(final ClassificationTrainingRow row) {
            double[] prediction = new double[m_data.length];
            for (int c = 0; c < m_data.length; c++) {
                double p = 0.0;
                for (int i = 0; i < m_data[c].length; i++) {
                    p += m_data[c][i] * row.getFeature(i);
                }
                prediction[c] = p;
            }
            return prediction;
        }
    }

    private class ScaledWeightVector extends AbstractWeightVector {
        private double m_scale;

        public ScaledWeightVector(final int nFets, final int nCats) {
            super(nFets, nCats);
            m_scale = 1.0;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void scale(final double alpha, final double lambda) {
            m_scale *= 1 - alpha * lambda;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void update(final double alpha, final double[][] d, final int nCovered) {
            updateData((final double val, final int c, final int i) -> val - alpha * d[c][i] / (m_scale * nCovered));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void checkNormalize() {
            if (m_scale > 1e100 || m_scale < -1e100 || (m_scale > 0 && m_scale < 1e-100) || (m_scale < 0 && m_scale > -1e-100)) {
                doFinalize();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public double[][] getWeightVector() {
            assert m_scale == 1.0 : "Finalize must be called before this method.";
            return super.getWeightVector();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void finalize(final double[][] d) {
            doFinalize();
        }

        private void doFinalize() {
            updateData((final double val, final int c, final int i) -> val * m_scale);
            m_scale = 1.0;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public double[] predict(final ClassificationTrainingRow row) {
            double[] prediction = super.predict(row);
            for (int c = 0; c < prediction.length; c++) {
                prediction[c] *= m_scale;
            }
            return null;
        }
    }

    /**
     * Simple implementation of a WeightVector that stores its values in a double array.
     *
     * @author Adrian Nembach, KNIME.com
     */
    private class SimpleWeightVector extends AbstractWeightVector {


        public SimpleWeightVector(final int nFets, final int nCats) {
            super(nFets, nCats);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void scale(final double alpha, final double lambda) {
            final double scalingFactor = 1 - alpha * lambda;
            updateData((final double val, final int c, final int i) -> val * scalingFactor);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void update(final double alpha, final double[][] d, final int nCovered) {
            updateData((final double val, final int c, final int i) -> val - alpha * d[c][i] / nCovered);
        }


        /**
         * {@inheritDoc}
         */
        @Override
        public void checkNormalize() {
            // nothing to do here

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void finalize(final double[][] d) {
            // nothing to do here
        }

    }

}
