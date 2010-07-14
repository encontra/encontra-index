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
package pt.inevo.encontra.nbtree.util;

import java.io.Serializable;
import java.util.Comparator;
import pt.inevo.encontra.nbtree.keys.Key;

/**
 * Comparator for comparing two keys
 * @author Ricardo Dias
 */
public class KeyComparator implements Comparator<Key>, Serializable {

    private static final long serialVersionUID = 2188424315140713587L;

    @Override
    public int compare(Key o1, Key o2) {
        return o1.compareTo(o2);
    }
}
