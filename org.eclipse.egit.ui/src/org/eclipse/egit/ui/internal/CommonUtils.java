/*******************************************************************************
 * Copyright (C) 2011, Dariusz Luksza <dariusz@luksza.org>
 * Copyright (C) 2011, Robin Stocker <robin@nibor.org>
 * Copyright (C) 2011, Bernard Leach <leachbj@bouncycastle.org>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.egit.ui.internal;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.CommandException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.ui.ISources;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;

/**
 * Class containing all common utils
 */
public class CommonUtils {

	private CommonUtils() {
		// non-instantiable utility class
	}

	/**
	 * Instance of comparator that sorts strings in ascending alphabetical and
	 * numerous order (also known as natural order).
	 */
	public static final Comparator<String> STRING_ASCENDING_COMPARATOR = new Comparator<String>() {
		public int compare(String o1, String o2) {
			if (o1.length() == 0)
				return -1;
			if (o2.length() == 0)
				return 1;

			LinkedList<String> o1Parts = splitIntoDigitAndNonDigitParts(o1);
			LinkedList<String> o2Parts = splitIntoDigitAndNonDigitParts(o2);

			Iterator<String> o2PartsIterator = o2Parts.iterator();

			for (String o1Part : o1Parts) {
				if (!o2PartsIterator.hasNext())
					return 1;

				String o2Part = o2PartsIterator.next();

				int result;

				if (Character.isDigit(o1Part.charAt(0)) && Character.isDigit(o2Part.charAt(0))) {
					o1Part = stripLeadingZeros(o1Part);
					o2Part = stripLeadingZeros(o2Part);
					result = o1Part.length() - o2Part.length();
					if (result == 0)
						result = o1Part.compareTo(o2Part);
				} else {
					result = o1Part.compareTo(o2Part);
				}

				if (result != 0)
					return result;
			}

			return -1;
		}
	};

	/**
	 * Instance of comparator which sorts {@link Ref} names using
	 * {@link CommonUtils#STRING_ASCENDING_COMPARATOR}.
	 */
	public static final Comparator<Ref> REF_ASCENDING_COMPARATOR = new Comparator<Ref>() {
		public int compare(Ref o1, Ref o2) {
			return STRING_ASCENDING_COMPARATOR.compare(o1.getName(), o2.getName());
		}
	};

	/**
	 * Programatically run command based on it id and given selection
	 *
	 * @param commandId
	 *            id of command that should be run
	 * @param selection
	 *            given selection
	 * @return {@code true} when command was successfully executed,
	 *         {@code false} otherwise
	 */
	public static boolean runCommand(String commandId,
			IStructuredSelection selection) {
		ICommandService commandService = (ICommandService) PlatformUI
				.getWorkbench().getService(ICommandService.class);
		Command cmd = commandService.getCommand(commandId);
		if (!cmd.isDefined())
			return false;

		IHandlerService handlerService = (IHandlerService) PlatformUI
				.getWorkbench().getService(IHandlerService.class);
		EvaluationContext c = null;
		if (selection != null) {
			c = new EvaluationContext(
					handlerService.createContextSnapshot(false),
					selection.toList());
			c.addVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME, selection);
			c.removeVariable(ISources.ACTIVE_MENU_SELECTION_NAME);
		}
		try {
			if (c != null)
				handlerService.executeCommandInContext(
						new ParameterizedCommand(cmd, null), null, c);
			else
				handlerService.executeCommand(commandId, null);

			return true;
		} catch (CommandException ignored) {
			// Ignored
		}
		return false;
	}

	private static LinkedList<String> splitIntoDigitAndNonDigitParts(
			String input) {
		LinkedList<String> parts = new LinkedList<String>();
		int partStart = 0;
		boolean previousWasDigit = Character.isDigit(input.charAt(0));
		for (int i = 1; i < input.length(); i++) {
			boolean isDigit = Character.isDigit(input.charAt(i));
			if (isDigit != previousWasDigit) {
				parts.add(input.substring(partStart, i));
				partStart = i;
				previousWasDigit = isDigit;
			}
		}
		parts.add(input.substring(partStart));
		return parts;
	}

	private static String stripLeadingZeros(String input) {
		for (int i = 0; i < input.length(); i++)
			if (input.charAt(i) != '0')
				return input.substring(i);
		return ""; //$NON-NLS-1$
	}
}
