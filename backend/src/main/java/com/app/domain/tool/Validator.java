package com.app.domain.tool;

import com.app.domain.exception.ValidateError.ValidationException;
import com.app.domain.model.Money;

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
