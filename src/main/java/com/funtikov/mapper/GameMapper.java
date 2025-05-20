package com.funtikov.mapper;

import com.funtikov.dto.game.GameSaveDto;
import com.funtikov.dto.game.GameStepSaveDto;
import com.funtikov.dto.game.OptionSaveDto;
import com.funtikov.entity.game.Game;
import com.funtikov.entity.game.GameStep;
import com.funtikov.entity.game.Option;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class GameMapper {

    /**
     * Маппим GameSaveDto -> Game (и вложенные шаги и опции).
     */
    public Game mapToGame(GameSaveDto dto) {
        if (dto == null) {
            return null;
        }

        // Создаём корневую сущность Game
        Game game = new Game();
        game.setBotStartCommand(dto.getBotStartCommand());

        // Список шагов (будет привязан к game)
        List<GameStep> steps = new ArrayList<>();

        // 1) Создаём Map: "DTO-шаг" -> "сущность GameStep"
        Map<String, GameStep> stepMapping = new HashMap<>();

        // --- Первый проход: создаём объекты GameStep (пока без Options) ---
        if (dto.getGameSteps() != null) {
            for (GameStepSaveDto stepDto : dto.getGameSteps()) {
                GameStep stepEntity = new GameStep();
                stepEntity.setGame(game);
                stepEntity.setStepOrder(stepDto.getStepOrder());
                stepEntity.setImageUrl(stepDto.getImageUrl());
                stepEntity.setCaption(stepDto.getCaption());

                // Запоминаем соответствие "DTO-шаг" -> "сущность"
                stepMapping.put(stepDto.getStepKey(), stepEntity);
                steps.add(stepEntity);
            }
        }
        // Привязываем список шагов к игре
        game.setGameSteps(steps);

        // --- Второй проход: создаём опции для каждого шага ---
        if (dto.getGameSteps() != null) {
            for (GameStepSaveDto stepDto : dto.getGameSteps()) {
                // Находим сущность-шаг, соответствующую DTO-шагу
                GameStep currentStepEntity = stepMapping.get(stepDto.getStepKey());
                if (currentStepEntity == null) {
                    // Если почему-то не нашли, пропускаем
                    continue;
                }

                List<Option> optionEntities = new ArrayList<>();
                if (stepDto.getOptions() != null) {
                    for (OptionSaveDto optionDto : stepDto.getOptions()) {
                        Option optionEntity = new Option();

                        // Для currentStep в Option берём текущий "родительский" шаг
                        optionEntity.setCurrentStep(currentStepEntity);

                        // Заполняем поля
                        optionEntity.setButtonText(optionDto.getButtonText());
                        optionEntity.setFinalOption(optionDto.isFinalOption());

                        // Если в optionDto.nextStep не null, пытаемся найти соответствующий шаг
                        if (optionDto.getNextStep() != null) {
                            GameStep nextStepEntity = stepMapping.get(optionDto.getNextStep().getStepKey());
                            optionEntity.setNextStep(nextStepEntity);
                        }

                        optionEntities.add(optionEntity);
                    }
                }

                // Привязываем список опций к сущности-шагу
                currentStepEntity.setOptions(optionEntities);
            }
        }

        return game;
    }
}
