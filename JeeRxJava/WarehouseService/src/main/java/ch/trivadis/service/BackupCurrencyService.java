package ch.trivadis.service;

/**
 * Created by Andy Moncsek on 25.09.15.
 */
public class BackupCurrencyService {


    public enum Currency {
        CHF(0.9);
        private double factor;

        Currency(double factor) {

        }
    }
}
