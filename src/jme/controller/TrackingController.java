/*
 * Created on Jul 10, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package jme.controller;

import java.util.logging.Level;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.vector.Vector3f;

import jme.entity.Entity;
import jme.entity.camera.Camera;
import jme.geometry.model.md3.Md3Model;
import jme.locale.external.data.AbstractHeightMap;
import jme.math.MathUtils;
import jme.physics.mobile.LandMobility;
import jme.system.KeyBindingManager;
import jme.utility.LoggingSystem;

/**
 * @author mpowell
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TrackingController extends AbstractGameController {
	private int idleAnim;
	private int currentAnimation;
	private LandMobility physics;
	private AbstractHeightMap hm;
	private Camera camera;
	//keybindings
	protected KeyBindingManager key;
	
	Vector3f newPos;
		double distance;

	
	public TrackingController(Entity entity, Camera camera) {
		this.entity = entity;
		this.camera = camera;
		try {
			Mouse.create();
			Keyboard.create();
		} catch (Exception e) {
			e.printStackTrace();
			LoggingSystem.getLoggingSystem().getLogger().log(
				Level.WARNING,
				"Error creating Mouse and/or Keyboard");

		}
		
		physics = new LandMobility();
		physics.setMaxVelocity(40.0f);
		physics.setMinVelocity(-20.0f);
		physics.setAcceleration(10.0f);
		physics.setDeceleration(20.0f);
		physics.setCurrentAngle(0);
		physics.setTurningVelocity(60.0f);
		
		setDefaultKeyBindings();
	}

	/* (non-Javadoc)
	 * @see jme.controller.EntityController#render()
	 */
	public void render() {
		camera.render();

	}

	public void setHeightMap(AbstractHeightMap hm) {
		this.hm = hm;
	}

	/* (non-Javadoc)
	 * @see jme.controller.AbstractGameController#update(float)
	 */
	public boolean update(float frameRate) {
		Keyboard.poll();
		physics.update(1/frameRate);
		camera.update(frameRate);
		camera.updateFrustum();
		physics.updatePosition(entity.getPosition());
		if(entity.getPosition().x > 2000) {
			entity.getPosition().x = 2000;
		}
		
		if(entity.getPosition().z > 2000) {
			entity.getPosition().z = 2000;
		}
		
		if(entity.getPosition().x < 200) {
			entity.getPosition().x = 200;
		}
		
		if(entity.getPosition().z < 200) {
			entity.getPosition().z = 200;
		}
		entity.getPosition().y = hm.getInterpolatedHeight(entity.getPosition().x/2, entity.getPosition().z/2) + 3;
		camera.setView(entity.getPosition());

		
		distance = MathUtils.distance(camera.getPosition(), entity.getPosition());

		if(distance > 30) {
			Vector3f vec =
				(
					Vector3f.sub(
						camera.getView(),
						camera.getPosition(),
						null)).normalise(
					null);

	
			camera.getPosition().x += vec.x * 1.00f;
			camera.getPosition().z += vec.z * 1.00f;
	
			if(camera.getPosition().x > 2000) {
				camera.getPosition().x = 2000;
			}

			if(camera.getPosition().z > 2000) {
				camera.getPosition().z = 2000;
			}

			if(camera.getPosition().x < 200) {
				camera.getPosition().x = 200;
			}

			if(camera.getPosition().z < 200) {
				camera.getPosition().z = 200;
			}
	
			camera.getPosition().y = hm.getInterpolatedHeight(camera.getPosition().x/2, camera.getPosition().z/2) + 8;
		}
		
		//update animation....
		if(physics.getCurrentVelocity() < 1) {
			if((physics.isRightTurn() || physics.isLeftTurn()) && currentAnimation != 0) {
				currentAnimation = 0;
				((Md3Model)entity.getGeometry()).setLegsAnimation("LEGS_TURN");
			} else if(!physics.isRightTurn() && !physics.isLeftTurn() && currentAnimation != 3){
				currentAnimation = 3;
				((Md3Model)entity.getGeometry()).setLegsAnimation("LEGS_IDLE");
			}
		} else if(physics.getCurrentVelocity() > 1 && physics.getCurrentVelocity() < 20 && currentAnimation != 1) {
			currentAnimation = 1;
			((Md3Model)entity.getGeometry()).setLegsAnimation("LEGS_WALK");
		} else if (physics.getCurrentVelocity() > 20 && currentAnimation != 2){
			currentAnimation = 2;
			((Md3Model)entity.getGeometry()).setLegsAnimation("LEGS_RUN");
		}
		if(isKeyDown("forward")) {
			physics.accelerate();
		} else {
			physics.decelerate();
		}
		
		if(isKeyDown("right")) {
			physics.turnRight();
			entity.setYaw(physics.getCurrentAngle());
		} else {
			if(isKeyDown("left")) {
				physics.turnLeft();
				entity.setYaw(physics.getCurrentAngle());
			} else {
				physics.noTurn();
			}
		}
		
		
		
		return checkAdditionalKeys();
	}
	
	/**
	 * <code>checkAdditionalKeys</code> tests the keyboard for any additional
	 * key bindings. It is recommended that this be overridden by any 
	 * subclass to add additional key bindings. The method returns a boolean. 
	 * This boolean notifies if the game should be stopped or not. Default false
	 * is generated by hitting the escape key.
	 * 
	 * @param boolean true continue for another frame, false stop.
	 */
	protected boolean checkAdditionalKeys() {
		if (isKeyDown("exit")) {
			System.out.println("EXIT");
			return false;
		}
		
		

		return true;
	}
	
	/**
	 * <code>setDefaultKeyBindings</code> sets the default bindings for
	 * forward, backward, strafe left/right, turn left/right, rise and lower.
	 */
	private void setDefaultKeyBindings() {
		key = KeyBindingManager.getKeyBindingManager();
		key.set("exit", Keyboard.KEY_ESCAPE);
		key.set("forward", Keyboard.KEY_W);
		key.set("right", Keyboard.KEY_D);
		key.set("left", Keyboard.KEY_A);
		key.set("back", Keyboard.KEY_S);
		key.add("forward", Keyboard.KEY_UP);
		key.add("right", Keyboard.KEY_RIGHT);
		key.add("left", Keyboard.KEY_LEFT);
	}

}
