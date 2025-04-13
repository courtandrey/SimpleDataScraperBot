package com.github.courtandrey.simpledatascraperbot.bot.step;

import com.github.courtandrey.simpledatascraperbot.bot.StateRegistry;

import java.util.function.BiFunction;

public class StepMappingFunction implements BiFunction<Integer, StateRegistry.DialogType, StepFunction> {

    @Override
    public StepFunction apply(Integer id, StateRegistry.DialogType dialogType) {
        if (id == null) throw new UnsupportedOperationException("Unknown step of dialog");
        return switch (dialogType) {
            case ADD_REQUEST -> getAddStepFunction(id);
            case STOP_ADMIN -> getStopAdmin(id);
            default -> throw new UnsupportedOperationException("Unsupported request for step mapping");
        };
    }

    private StepFunction getAddStepFunction(Integer id) {
        return switch (id) {
            case 0 -> new Step0Add();
            case 10 -> new Step10Add();
            case 11 -> new Step11Add();
            case 12 -> new Step12Add();
            case 13 -> new Step13Add();
            case 20 -> new Step20Add();
            case 21 -> new Step21Add();
            case 30 -> new Step30Add();
            case 31 -> new Step31Add();
            case 32 -> new Step32Add();
            case 33 -> new Step33Add();
            case 34 -> new Step34Add();
            case 35 -> new Step35Add();
            case 40 -> new Step40Add();
            case 50 -> new Step50Add();
            case 51 -> new Step51Add();
            case 52 -> new Step52Add();
            case 53 -> new Step53Add();
            default -> throw new UnsupportedOperationException("Unknown step of dialog");
        };
    }

    private StepFunction getStopAdmin(Integer id) {
        return switch (id) {
            case 0 -> new Step0StopAdmin();
            default -> throw new UnsupportedOperationException("Unknown step of dialog");
        };
    }
}
