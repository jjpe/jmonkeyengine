/*
 * Copyright (c) 2003, jMonkeyEngine - Mojo Monkey Coding
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this 
 * list of conditions and the following disclaimer. 
 * 
 * Redistributions in binary form must reproduce the above copyright notice, 
 * this list of conditions and the following disclaimer in the documentation 
 * and/or other materials provided with the distribution. 
 * 
 * Neither the name of the Mojo Monkey Coding, jME, jMonkey Engine, nor the 
 * names of its contributors may be used to endorse or promote products derived 
 * from this software without specific prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

package jme.entity.effects;

import org.lwjgl.vector.Vector3f;

/**
 * <code>Particle</code> represents a single particle that is part of a larger
 * particle emitter. It maintains the information required to model an object
 * traveling in space. Things such as position, velocity, color, size are all
 * contained within the <code>Particle</code> class.
 * 
 * 
 * @author Mark Powell
 * @version 1
 */
public class Particle {
   
    /**
     * denotes the amount of life left for the particlular particle.
     * Full health is 1 and dead is 0. The amount the particle 
     * is drained each update is dependant on the fade value.
     */
    public float life;
    /**
     * denotes the amount to degrade the particles life each turn.
     * This value should be between 0 and 1.
     */
    public float fade;
    /**
     * the color of the particle in RGB format.
     */
    public Vector3f color;
    /**
     * the location in 3D space of the particle.
     */
    public Vector3f position;
    /**
     * the direction the particle is traveling in.
     */
    public Vector3f velocity;
    /**
     * the size of the particle.
     */
    public Vector3f size;
    
    /**
     * Constructor instantiates a new <code>Particle</code> and
     * initializes all the variables.
     */
    public Particle() {
        color = new Vector3f();
        position = new Vector3f();
        velocity = new Vector3f();
        size = new Vector3f();
    }
    
    public String toString() {
    	return "life: " + life + " fade: " + fade + " color: " + color.toString() +
    	" position: " + position.toString() + " velocity: " + velocity.toString() +
    	" size: " + size.toString();
    }
    
}