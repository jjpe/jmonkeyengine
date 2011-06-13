/*
 * Copyright (c) 2009-2010 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jme3.export;

import com.jme3.effect.shapes.EmitterBoxShape;
import com.jme3.effect.shapes.EmitterMeshConvexHullShape;
import com.jme3.effect.shapes.EmitterMeshFaceShape;
import com.jme3.effect.shapes.EmitterMeshVertexShape;
import com.jme3.effect.shapes.EmitterPointShape;
import com.jme3.effect.shapes.EmitterSphereShape;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme3.export.InputCapsule;
import com.jme3.export.Savable;
import com.jme3.material.MatParamTexture;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * <code>SavableClassFinder</code> is used to find classes referenced
 * by savables.
 * Currently it will remap any classes from old paths to new paths
 * so that old J3O models can still be loaded.
 *
 * @author mpowell
 * @author Kirill Vainer
 */
public class SavableClassFinder {

    private final static HashMap<String, String> classRemappings = new HashMap<String, String>();

    private static void addRemapping(String oldClass, Class<? extends Savable> newClass){
        classRemappings.put(oldClass, newClass.getName());
    }
    
    static {
        addRemapping("com.jme3.effect.EmitterSphereShape", EmitterSphereShape.class);
        addRemapping("com.jme3.effect.EmitterBoxShape", EmitterBoxShape.class);
        addRemapping("com.jme3.effect.EmitterMeshConvexHullShape", EmitterMeshConvexHullShape.class);
        addRemapping("com.jme3.effect.EmitterMeshFaceShape", EmitterMeshFaceShape.class);
        addRemapping("com.jme3.effect.EmitterMeshVertexShape", EmitterMeshVertexShape.class);
        addRemapping("com.jme3.effect.EmitterPointShape", EmitterPointShape.class);
        addRemapping("com.jme3.material.Material$MatParamTexture", MatParamTexture.class);
    }
    
    private static String remapClass(String className) throws ClassNotFoundException {
        String result = classRemappings.get(className);
        if (result == null) {
            return className;
        } else {
            return result;
        }
    }

    /**
     * fromName creates a new Savable from the provided class name. First registered modules
     * are checked to handle special cases, if the modules do not handle the class name, the
     * class is instantiated directly. 
     * @param className the class name to create.
     * @param inputCapsule the InputCapsule that will be used for loading the Savable (to look up ctor parameters)
     * @return the Savable instance of the class.
     * @throws InstantiationException thrown if the class does not have an empty constructor.
     * @throws IllegalAccessException thrown if the class is not accessable.
     * @throws ClassNotFoundException thrown if the class name is not in the classpath.
     * @throws IOException when loading ctor parameters fails
     */
    public static Savable fromName(String className, InputCapsule inputCapsule) throws InstantiationException,
            IllegalAccessException, ClassNotFoundException, IOException {

        className = remapClass(className);
        try {
            return (Savable) Class.forName(className).newInstance();
        } catch (InstantiationException e) {
            Logger.getLogger(SavableClassFinder.class.getName()).log(
                    Level.SEVERE, "Could not access constructor of class ''{0}" + "''! \n"
                    + "Some types need to have the BinaryImporter set up in a special way. Please doublecheck the setup.", className);
            throw e;
        } catch (IllegalAccessException e) {
            Logger.getLogger(SavableClassFinder.class.getName()).log(
                    Level.SEVERE, "{0} \n"
                    + "Some types need to have the BinaryImporter set up in a special way. Please doublecheck the setup.", e.getMessage());
            throw e;
        }
    }

    public static Savable fromName(String className, InputCapsule inputCapsule, List<ClassLoader> loaders) throws InstantiationException,
            IllegalAccessException, ClassNotFoundException, IOException {
        if (loaders == null) {
            return fromName(className, inputCapsule);
        }
        
        String newClassName = remapClass(className);
        for (ClassLoader classLoader : loaders){
            try {
                return (Savable) classLoader.loadClass(newClassName).newInstance();
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            }

        }

        return fromName(className, inputCapsule);
    }
}