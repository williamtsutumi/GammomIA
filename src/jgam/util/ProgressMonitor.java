/*
 * JGammon: A backgammon client written in Java
 * Copyright (C) 2005/06 Mattias Ulbrich
 *
 * JGammon includes: - playing over network
 *                   - plugin mechanism for graphical board implementations
 *                   - artificial intelligence player
 *                   - plugin mechanism for AI players
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */package jgam.util;

import java.awt.Component;

/**
 * This is only the usual ProgressMonitor 
 *
 *Feature:
 *  you can read the current progress value too!
 *  
 * @author Mattias Ulbrich
 */
public class ProgressMonitor extends javax.swing.ProgressMonitor {

    public ProgressMonitor(Component parentComponent, Object message,
            String note, int min, int max) {
        super(parentComponent, message, note, min, max);
    }
    
    private int progress;
    
    public void setProgress(int val) {
        super.setProgress(val);
        progress = val;
    }
    
    public int getProgress() {
        return progress;
    }

}
