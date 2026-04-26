package auction.tool;

import auction.exception.ValidateError.ValidationException;

import auction.model.Money;

public class Validator {
    
    public static void validateBid(Money amount) throws ValidationException {

        if (amount == null){
            throw new ValidationException("Amount is null");
        }

        if (amount.getAmount().doubleValue() <= 0){
            throw new ValidationException("Amount must > 0");
        }
    }
}
