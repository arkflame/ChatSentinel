package dev._2lstudios.chatsentinel.shared.modules;

public abstract class ModuleManager {
	private CapsModule capsModule;
	private CooldownModule cooldownModule;
	private FloodModule floodModule;
	private MessagesModule messagesModule;
	private GeneralModule generalModule;
	private BlacklistModule blacklistModule;
	private SyntaxModule syntaxModule;
	private WhitelistModule whitelistModule;

	public ModuleManager() {
		this.capsModule = new CapsModule();
		this.cooldownModule = new CooldownModule();
		this.floodModule = new FloodModule();
		this.blacklistModule = new BlacklistModule();
		this.syntaxModule = new SyntaxModule();
		this.messagesModule = new MessagesModule();
		this.generalModule = new GeneralModule();
		this.whitelistModule = new WhitelistModule();
	}

	public CooldownModule getCooldownModule() {
		return cooldownModule;
	}

	public CapsModule getCapsModule() {
		return capsModule;
	}

	public FloodModule getFloodModule() {
		return floodModule;
	}

	public BlacklistModule getBlacklistModule() {
		return blacklistModule;
	}

	public SyntaxModule getSyntaxModule() {
		return syntaxModule;
	}

	public MessagesModule getMessagesModule() {
		return messagesModule;
	}

	public GeneralModule getGeneralModule() {
		return generalModule;
	}

	public WhitelistModule getWhitelistModule() {
		return whitelistModule;
	}

	public abstract void reloadData();
}
