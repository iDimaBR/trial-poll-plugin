package com.github.idimabr;

import com.github.idimabr.commands.CreatePollCommand;
import com.github.idimabr.commands.PollCommand;
import com.github.idimabr.controllers.PollController;
import com.github.idimabr.menus.OptionMenu;
import com.github.idimabr.menus.PollMenu;
import com.github.idimabr.menus.ResultsMenu;
import com.github.idimabr.storage.Database;
import com.github.idimabr.storage.dao.PollRepository;
import com.github.idimabr.tasks.ExpirePollTask;
import com.github.idimabr.util.ConfigUtil;
import com.henryfabio.minecraft.inventoryapi.manager.InventoryManager;
import com.henryfabio.sqlprovider.connector.SQLConnector;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class TrialPoll extends JavaPlugin {

    private SQLConnector connection;
    private PollController controller;
    private PollRepository repository;
    private ConfigUtil config;
    private ConfigUtil messages;

    @Override
    public void onLoad() {
        this.config = new ConfigUtil(this, "config.yml");
        this.messages = new ConfigUtil(this, "messages.yml");
    }

    @Override
    public void onEnable() {
        loadStorage();
        loadControllers();
        loadInventories();
        loadCommands();
        loadTasks();
    }

    @Override
    public void onDisable() {

    }

    private void loadStorage() {
        connection = new Database(this).createConnector(config.getConfigurationSection("Database"));
        repository = new PollRepository(this, connection);
        repository.createTables();
    }

    private void loadControllers(){
        this.controller = new PollController(repository);
    }

    private void loadCommands(){
        getCommand("createpoll").setExecutor(new CreatePollCommand(this, controller));
        getCommand("poll").setExecutor(new PollCommand(messages, controller));
    }

    private void loadTasks(){
        new ExpirePollTask(this, controller).runTaskTimer(this, 0L, config.getInt("interval-poll", 5) * 20L);
    }

    private void loadInventories(){
        InventoryManager.enable(this);
        new PollMenu(
                this,
                config.getInt("menus.poll.row") * 9,
                config.getString("menus.poll.title")
        ).init();
        new ResultsMenu(
                this,
                config.getInt("menus.results.row") * 9,
                config.getString("menus.results.title")
        ).init();
        new OptionMenu(
                this,
                config.getInt("menus.options.row") * 9,
                config.getString("menus.options.title")
        ).init();
    }
}
