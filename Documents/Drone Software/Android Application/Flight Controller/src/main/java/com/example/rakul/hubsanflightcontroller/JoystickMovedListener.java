package com.example.rakul.hubsanflightcontroller;

/*
 * @author: Richard Clapham
 * @author: Rakul Mahenthiran
 * @date: 4/8/2016
 * Interface designed to listen for when the joystick is moved
 */

public interface JoystickMovedListener {
	public void OnMoved(int pan, int tilt);
	public void OnReleased();
	public void OnReturnedToCenter();
}
