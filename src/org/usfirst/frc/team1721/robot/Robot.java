package org.usfirst.frc.team1721.robot;

import org.usfirst.frc.team1721.robot.subsystems.Autonomous;
import org.usfirst.frc.team1721.robot.subsystems.DriveTrain;
import org.usfirst.frc.team1721.robot.subsystems.TeleOp;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {

	
	public static OI oi; 
	
	public static DriveTrain dt;

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		
		oi = new OI();
		dt = new DriveTrain();
		//Initialize drive Talons
		RobotMap.vspLeft = new WPI_TalonSRX(RobotMap.dtLeft);
		RobotMap.vspRight = new WPI_TalonSRX(RobotMap.dtRight);
		//Initialize drive slave Victors
		RobotMap.slaveLeft = new WPI_VictorSPX(RobotMap.slaveDriveLeft);
		RobotMap.slaveRight = new WPI_VictorSPX(RobotMap.slaveDriveRight);
		//Set drive slaves to follower mode
		RobotMap.slaveLeft.follow(RobotMap.vspLeft);
		RobotMap.slaveRight.follow(RobotMap.vspRight);
		//Initialize drive train
		RobotMap.rd = new DifferentialDrive(RobotMap.vspLeft, RobotMap.vspRight);
		//Initialize drive joystick
		RobotMap.stick = new Joystick(RobotMap.joystickPort);
		//Disabled drive safety
		RobotMap.vspLeft.setSafetyEnabled(false);
		RobotMap.vspRight.setSafetyEnabled(false);
		RobotMap.rd.setSafetyEnabled(false);
		//Initialize lift Talon
		RobotMap.liftTalon = new WPI_TalonSRX(RobotMap.liftTalonAddress);
		//Initialize operator controller
		RobotMap.controller = new Joystick(RobotMap.controllerPort);
		//Initialize intake Victors
		RobotMap.intakeVictorLeft = new WPI_VictorSPX(RobotMap.intakeMaster);
		RobotMap.intakeVictorRight = new WPI_VictorSPX(RobotMap.intakeSlave);
		//Set right intake Victor to follow left intake Victor
		RobotMap.intakeVictorRight.follow(RobotMap.intakeVictorLeft);
	}

	/**
	 * This function is called once each time the robot enters Disabled mode.
	 * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
	 */
	@Override
	public void disabledInit() {

	}

	@Override
	public void disabledPeriodic() {
		Scheduler.getInstance().run();
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString code to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional commands to the
	 * chooser code above (like the commented example) or additional comparisons
	 * to the switch structure below with additional strings & commands.
	 */
	@Override
	public void autonomousInit() {
		Autonomous.showSwitchPositions();
		
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		Scheduler.getInstance().run();
	}

	@Override
	public void teleopInit() {
		while(isEnabled() && isOperatorControl()){ // Runs periodically (every 25 ms) during teleop
			DriveTrain.driveWithJoystick(RobotMap.stick, RobotMap.rd); // Drive
			TeleOp.RaiseLift(RobotMap.liftTalon, RobotMap.controller); // Raise lift
			if(RobotMap.controller.getRawAxis(2) > 0) { // Ensures brake mode does not stop the lift from rising
			TeleOp.LowerLift(RobotMap.liftTalon, RobotMap.controller);// Lower lift
			}
			TeleOp.IntakeCube(RobotMap.intakeVictorLeft, RobotMap.controller);// Intakes and expels cubes
			Timer.delay(0.005);
		}
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		Scheduler.getInstance().run();
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
		LiveWindow.run();
	}
}
