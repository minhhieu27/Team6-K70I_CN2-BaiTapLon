package auction.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import auction.model.Bid;
import auction.model.Money;

public class FormatUtil {
    
    private static final DecimalFormat df = new DecimalFormat("#,###");

    public static string formatMoney(Money money) {
        return df.format(money.getAmount().setScale(2, RoundingMode.HALF_UP).toString() + " USD");
    }

    public static String formatBid(Bid bid){
        return bid.getBidder() + " bid " + formatMoney(bid.getAmount());
    }
}
