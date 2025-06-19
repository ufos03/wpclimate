package com.wpclimate.gui;

import com.wpclimate.core.AppContext;

/**
 * Interface for components that provide access to an AppContext
 */
public interface AppContextProvider {
    /**
     * Gets the current application context
     * @return The AppContext, or null if none is available
     */
    AppContext getAppContext();
}