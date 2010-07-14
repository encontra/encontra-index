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

import java.io.Serializable;

/**
 * Generic Key class.
 * Represents a pair (keyValue, keyId) <- this is the Key Object
 * @author Ricardo
 */
public class Key implements Serializable, Comparable {

    protected double keyValue;
    protected String keyId;

    public Key(){}

    public Key(double value, String id){
        this.keyValue = value;
        this.keyId = id;
    }

    public double getValue(){
        return keyValue;
    }

    public void setValue(double keyValue){
        this.keyValue = keyValue;
    }

    public String getId() {
        return keyId;
    }

    public void setId(String id){
        this.keyId = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Key){
            Key key = (Key)obj;
            if (keyValue == key.getValue() && keyId.equals(key.getId())){
                return true;
            }
            return false;
        }
        else return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.keyValue) ^ (Double.doubleToLongBits(this.keyValue) >>> 32));
        hash = 79 * hash + (this.keyId != null ? this.keyId.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString(){
        return "[Value: " + keyValue + ", Id: " + keyId + "]";
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Key){
            Key k = (Key)o;
            if (k.getValue() == keyValue && k.getId().equals(keyId)){
                return 0;
            } else if (k.getValue() == keyValue && !k.getId().equals(keyId)){
                return -1;
            } else {
                Double o1D = new Double(keyValue);
                Double o2D = new Double(k.getValue());
                return o1D.compareTo(o2D);
            }
        }
        return 1;
    }
}