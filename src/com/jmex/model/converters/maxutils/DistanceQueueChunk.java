/*
 * Copyright (c) 2003-2008 jMonkeyEngine
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

package com.jmex.model.converters.maxutils;

import java.io.DataInput;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Started Date: Jul 3, 2004<br><br>
 *
 * parent == 3d3d == EDIT_3DS
 * type == 2300 == DISTANCE_QUEUE
 *
 * @author Jack Lindamood
 */
class DistanceQueueChunk extends ChunkerClass{
    private static final Logger logger = Logger
            .getLogger(DistanceQueueChunk.class.getName());
    
    boolean activeDistanceQueue;
    float nearPlane;
    float nearDensity;
    float farPlane;
    float farDensity;

    public DistanceQueueChunk(DataInput myIn, ChunkHeader header) throws IOException {
        super(myIn, header);
    }

    protected void initializeVariables() throws IOException{
        nearPlane=myIn.readFloat();
        nearDensity=myIn.readFloat();
        farPlane=myIn.readFloat();
        farDensity=myIn.readFloat();
        if (DEBUG)
            logger.info("@distanceQueue nearPlane:"+nearPlane+" nearDensity:"+
                    nearDensity+" farPlane"+farPlane+" farDensity"+farDensity);
        decrHeaderLen(4*4);
    }

    protected boolean processChildChunk(ChunkHeader i) throws IOException {
        switch (i.type){
            case DQUEUE_BACKGRND:
                activeDistanceQueue=true;
                if (DEBUG) logger.info("Use distanceQueue true");
                return true;
            default:
                return false;
        }
    }
}
