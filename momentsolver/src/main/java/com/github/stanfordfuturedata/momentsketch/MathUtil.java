package com.github.stanfordfuturedata.momentsketch;

public class MathUtil {
    public static long[][] getBinomials(int m) {
        long[][] binoms = new long[m+1][m+1];
        for (int i = 0; i<=m; i++) {
            binoms[i][0] = 1;
            for (int j=1; j<=i; j++) {
                binoms[i][j] = binoms[i-1][j-1] + binoms[i-1][j];
            }
        }
        return binoms;
    }

    public static int[][] getChebyCoefficients(int k) {
        int[][] chebyCoeffs = new int[k+1][k+1];
        chebyCoeffs[0][0] = 1;
        if (k == 0) {
            return chebyCoeffs;
        }
        chebyCoeffs[1][1] = 1;
        for (int i = 2; i <= k; i++) {
            int[] c1 = chebyCoeffs[i-1];
            int[] c2 = chebyCoeffs[i-2];
            chebyCoeffs[i][0] = -c2[0];
            for (int j = 1; j <= i; j++) {
                chebyCoeffs[i][j] = 2*c1[j-1] - c2[j];
            }
        }
        return chebyCoeffs;
    }

    /**
     * Calculate power sums for shifted values x' = (x-xc)/r
     * @param powerSums original power sums
     * @param r scaling factor
     * @param xc translation value
     * @return shifted and scaled power sums
     */
    public static double[] shiftPowerSum(
            double[] powerSums,
            double r,
            double xc
    ) {
        int k = powerSums.length - 1;
        double[] scaledPowerSums = new double[k+1];
        // (-xc)**i
        double[] nxcPowers = new double[k+1];
        // r**(-i)
        double[] rNegPowers = new double[k+1];
        nxcPowers[0] = 1;
        rNegPowers[0] = 1;
        for (int i = 1; i <= k; i++) {
            nxcPowers[i] = nxcPowers[i-1] * (-xc);
            rNegPowers[i] = rNegPowers[i-1] / r;
        }
        long[][] mBinoms = MathUtil.getBinomials(k);
        for (int m = 0; m <= k; m++) {
            double sum = 0.0;
            for (int j = 0; j<=m; j++) {
                sum += mBinoms[m][j]*nxcPowers[m-j]*powerSums[j];
            }
            scaledPowerSums[m] = rNegPowers[m] * sum;
        }
        return scaledPowerSums;
    }


    public static double[] powerSumsToZerodMoments(
            double[] powerSums,
            double min,
            double max
    ) {
        double r = (max - min) / 2;
        double xc = (max + min) / 2;
        double[] scaledPowerSums = MathUtil.shiftPowerSum(
                powerSums,r,xc
        );
        double count = scaledPowerSums[0];
        for (int i = 0; i < powerSums.length; i++) {
            scaledPowerSums[i] /= count;
        }
        return scaledPowerSums;
    }


    public static double[] powerSumsToChebyMoments(
            double min,
            double max,
            double[] powerSums
    ) {
        int k = powerSums.length - 1;
        double r = (max - min) / 2;
        double xc = (max + min) / 2;
        // First rescale the variables so that they lie in [-1,1]
        double[] scaledPowerSums = MathUtil.shiftPowerSum(
                powerSums,r,xc
        );

        double count = powerSums[0];
        int[][] cCoeffs = MathUtil.getChebyCoefficients(k);
        // Then convert from power sums to chebyshev moments
        double[] scaledChebyMoments = new double[k+1];
        for (int i = 0; i <=k; i++) {
            double sum = 0.0;
            for (int j = 0; j <= i; j++) {
                sum += cCoeffs[i][j]*scaledPowerSums[j];
            }
            scaledChebyMoments[i] = sum / count;
        }

        return scaledChebyMoments;
    }

    public static double arrayMean(double[] xs) {
        double sum = 0.0;
        for (double x : xs) {
            sum += x;
        }
        return sum / xs.length;
    }

    public static void calcPowers(double x, double[] powers) {
        int n = powers.length;
        double curPow = 1.0;
        for (int i = 0; i < n; i++) {
            powers[i] = curPow;
            curPow *= x;
        }
        return;
    }

    public static double entropy(double[] ps) {
        double h = 0.0;
        for (double p : ps) {
            if (p > 0.0) {
                h -= p * Math.log(p);
            }
        }
        return h;
    }

    public static double getMSE(double[] error) {
        double sum = 0.0;
        for (int i = 0; i < error.length; i++) {
            sum += error[i]*error[i];
        }
        return sum / error.length;
    }

}
