package com.funtikov.endpoint.qute;

import com.funtikov.dto.game.GameSaveDto;
import com.funtikov.entity.game.Game;
import com.funtikov.service.GameService;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path(("/game"))
public class GameEndpoint {

    @Inject
    GameService gameService;

    @Inject
    Template gameSave;

    @Inject
    Template games;

    @Inject
    Template gameDetails;

    /**
     * Показываем форму создания игры.
     */
    @GET
    @Path("/save")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance showCreateGameForm() {
        // ботская команда изначально пустая
        return gameSave.data("botStartCommand", "");
    }

    /**
     * Принимаем JSON из JS и сохраняем игру.
     * Возвращаем 200 OK, JS перенаправит на /game/all.
     */
    @POST
    @Path("/save")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response saveGame(GameSaveDto dto) {
        gameService.createGame(dto);
        return Response.ok().entity("OK").build();
    }

    /**
     * Удаляем игру и сразу возвращаем обновлённый список.
     */
    @GET
    @Path("/delete/{botStartCommand}")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance deleteGame(@PathParam("botStartCommand") String botStartCommand) {
        gameService.deleteGameByBotStartCommand(botStartCommand);
        List<Game> all = gameService.findAll();
        return games.data("games", all);
    }

    /**
     * Отображаем список всех игр.
     */
    @GET
    @Path("/all")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance getAllGames() {
        List<Game> games = gameService.findAll();
        return this.games.data("games", games);
    }

    /**
     * Подробности конкретной игры.
     */
    @GET
    @Path("/details/{id}")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance getGameDetails(@PathParam("id") Long id) {
        Game game = gameService.findById(id);
        return gameDetails.data("game", game);
    }
}
