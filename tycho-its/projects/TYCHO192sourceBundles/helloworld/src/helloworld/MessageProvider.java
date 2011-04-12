/*******************************************************************************
 * Copyright (c) 2010, 2011 SAP AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     SAP AG - initial API and implementation
 *******************************************************************************/
package helloworld;

public class MessageProvider {

    public String getGreeting() {
        return getGreeting("World");
    }

    public String getGreeting(String receiver) {
        return "Hello " + receiver + "!!";
    }

}
