package dev._2lstudios.chatsentinel.shared.utils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;
import org.bukkit.entity.Player.Spigot;

public final class ReflectionUtil {
	private static final MethodHandle getLocalePlayerMethod = localePlayer();
	private static final MethodHandle getLocaleSpigotMethod = localeSpigot();
	private static final MethodHandle getHandleMethod = handleMethod();
	private static Field pingField = null;

	private static MethodHandle localePlayer() {
		try {
			MethodHandles.Lookup lookup = MethodHandles.publicLookup();
			return lookup.findVirtual(Player.class, "getLocale", MethodType.methodType(String.class));
		} catch (NoSuchMethodException | IllegalAccessException e) {
			return null;
		}
	}

	private static MethodHandle localeSpigot() {
		try {
			MethodHandles.Lookup lookup = MethodHandles.publicLookup();
			return lookup.findVirtual(Spigot.class, "getLocale", MethodType.methodType(String.class));
		} catch (NoSuchMethodException | IllegalAccessException e) {
			return null;
		}
	}

	private static MethodHandle handleMethod() {
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		try {
			Method method = Player.class.getMethod("getHandle");
			method.setAccessible(true);
			return lookup.unreflect(method);
		} catch (IllegalAccessException | NoSuchMethodException | SecurityException e) {
			return null;
		}
	}

	public static MethodHandle getLocalePlayerMethod() {
		return getLocalePlayerMethod;
	}

	public static MethodHandle getLocaleSpigotMethod() {
		return getLocaleSpigotMethod;
	}

	public static MethodHandle getHandleMethod() {
		return getHandleMethod;
	}

	public static Field getPingField(Object playerHandle) throws NoSuchFieldException, SecurityException {	
		return pingField == null ? pingField = playerHandle.getClass().getField("ping") : pingField;
	}

	private ReflectionUtil() {}
}
