/*
 * The GPLv3 licence :
 * -----------------
 * Copyright (c) 2010 Ricardo Dias
 *
 * This file is part of MuVis.
 *
 * MuVis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MuVis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MuVis.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.inevo.encontra.nbtree.keys;

import pt.inevo.encontra.nbtree.NBPoint;

/**
 * Generic interface for building an object that knows how to calculate
 * a key for a supplied point.
 * @author ricardo
 */
public interface KeyMapper {

    /**
     * Gets the Key for the point
     * @param point the supplied point
     * @return the key for the point
     */
    public Key getKey(NBPoint point);
}
