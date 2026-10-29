/*
 * This file includes a teleop (driver-controlled) file for the DinoByte! goBILDA® Robot for the
 * 2026-2027 FIRST® Tech Challenge season BIOBUZZ™!
 */

package org.firstinspires.ftc.teamcode.TeleOp1;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

//Camera Imports

//LimeLight

//Logitech


// Camera Imports

@TeleOp(name="TeleOpTesting", group="teamcode")

public class TeleOpTesting extends OpMode
{
    // Declare OpMode members.
    final double BOOTCLOSE_TIME_SECONDS = 0.3; //The boot servo waits this long before closing when a shot is requested.
    final double BOOTOPEN_TIME_SECONDS = 0.5; //The boot servo run this long before opening after making a shot.

    final double STOP_SPEED = 0.0; //We send this power to the servos and DCs when we want them to stop.
    final double FULL_SPEED = 1.0;

    final double LAUNCHER_CLOSE_TARGET_VELOCITY = 1380.0; //in ticks/second for the close goal.
    final double LAUNCHER_CLOSE_MIN_VELOCITY = 1360.0; //minimum required to start a shot for close goal.
    //*******//
    final double LAUNCHER_CLOSEWALL_TARGET_VELOCITY = 1315.0; //in ticks/second for the close goal.
    final double LAUNCHER_CLOSEWALL_MIN_VELOCITY = 1300.0; //minimum required to start a shot for close goal.

    final double LAUNCHER_FAR_TARGET_VELOCITY = 1600.0; //Target velocity for far goal
    final double LAUNCHER_FAR_MIN_VELOCITY = 1575.0; //minimum required to start a shot for far goal.

    final double LAUNCHER_REVERSE_TARGET_VELOCITY = -400.0; //in ticks/second during Intake.
    final double LAUNCHER_REVERSE_MIN_VELOCITY = -410.0; //in ticks/second during Intake.

    double launcherTarget = LAUNCHER_CLOSE_TARGET_VELOCITY; //These variables allow
    double launcherMin = LAUNCHER_CLOSE_MIN_VELOCITY;

    final double RRBOOTOPEN_POSITION = 1.0; //the open and close position for the rear boot servo
    final double RRBOOTGATE_POSITION = 0.6;
    final double RRBOOTCLOSE_POSITION = 0.2;

    //Declare timers for Boot Close and Open
    ElapsedTime bootCloseTimer = new ElapsedTime();
    ElapsedTime bootOpenTimer = new ElapsedTime();
    ElapsedTime sleepTimer = new ElapsedTime();
    ElapsedTime runtime = new ElapsedTime();

    private DcMotor leftFrontDrive = null;
    private DcMotor rightFrontDrive = null;
    private DcMotor leftBackDrive = null;
    private DcMotor rightBackDrive = null;

    private DcMotor intake = null;

    private DcMotorEx launcher = null;

    private DcMotorEx flowerElevator = null;

    private Servo rearBoot = null;

    private CRServo leftSideIntake = null;

    private CRServo rightSideIntake = null;

    private Servo lightServo = null;

    private Servo flowerArm = null;

    private Servo flowerWrist = null;

    private enum IntakeState {
        ON,
        OFF;
    }

    private IntakeState intakeState = IntakeState.OFF;

    private enum LauncherDistance {
        CLOSE,
        FAR,
        REVERSE;
    }

    private LauncherDistance launcherDistance = LauncherDistance.CLOSE;

    private enum LaunchState {
        IDLE,
        BOOTOPEN,
        BOOTCLOSE,
        BOOTOPEN2,
        BOOTCLOSE2,
        BOOTOPEN3,
        BOOTCLOSE3,
        BOOTOPEN4,
        BOOTCLOSE4,
        BOOTOPEN5,
        BOOTCLOSE5;
    }

    private LaunchState leftLaunchState;
    private LaunchState rightLaunchState;

    private enum RearBootState {
        RRBOOTOPEN,
        RRBOOTCLOSE;
    }
    private RearBootState rearBootState = RearBootState.RRBOOTOPEN;

    private LaunchState launchState;

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        telemetry.addData("Status", "Initialized");

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        leftFrontDrive = hardwareMap.get(DcMotor.class, "leftFrontDrive");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "rightFrontDrive");
        leftBackDrive = hardwareMap.get(DcMotor.class, "leftBackDrive");
        rightBackDrive = hardwareMap.get(DcMotor.class, "rightBackDrive");

        intake = hardwareMap.get(DcMotor.class, "intake");

        launcher = hardwareMap.get(DcMotorEx.class, "launcher");

        rearBoot = hardwareMap.get(Servo.class, "rearFeeder");

        flowerElevator = hardwareMap.get(DcMotorEx.class, "flowerElevator");

        rearBoot = hardwareMap.get(Servo.class, "rearFeeder");

        leftSideIntake = hardwareMap.get(CRServo.class, "leftSideIntake");

        rightSideIntake = hardwareMap.get(CRServo.class, "rightSideIntake");

        lightServo = hardwareMap.get(Servo.class, "lightServo");

        flowerArm = hardwareMap.get(Servo.class, "flowerArm");

        flowerWrist = hardwareMap.get(Servo.class, "flowerWrist");
        //rightCatapult = hardwareMap.get(DcMotor.class, "rightCatapult");

        // To drive forward, most robots need the motor on one side to be reversed, because the axles point in opposite directions.
        // Pushing the left stick forward MUST make robot go forward. So adjust these two lines based on your first test drive.
        // Note: The settings here assume direct drive on left and right wheels.  Gear Reduction or 90 Deg drives may require direction flips
        leftFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        rightFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        leftBackDrive.setDirection(DcMotor.Direction.FORWARD);
        rightBackDrive.setDirection(DcMotor.Direction.REVERSE);

        intake.setDirection(DcMotorSimple.Direction.REVERSE);

        launcher.setDirection(DcMotorSimple.Direction.REVERSE);

        // Reset the encoder during initialization
        launcher.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        //flowerElevator.setTargetPosition(600);
        //flowerElevator.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        //flowerElevator.setPower(0.5);
        flowerElevator.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Use RUN_USING_ENCODER on Launcher for manual control and setup PIDF Coef.
        launcher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        flowerElevator.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //launcher.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(1000, 0, 40, 41));

        /*
         * Setting zeroPowerBehavior to BRAKE enables a "brake mode". This causes the motor to
         * slow down much faster when it is coasting. This creates a much more controllable
         * drivetrain. As the robot stops much quicker.
         */
        leftFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        launcher.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        flowerElevator.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Initialize the Catapult
        //catapult_init();

        /*
         * set gates (elevator) and boot initial positions
         */
        rearBoot.setPosition(RRBOOTGATE_POSITION);

        //flowerArm.setPosition(200);

        flowerWrist.setPosition(0.0);

        leftLaunchState = LaunchState.IDLE;
        rightLaunchState = LaunchState.IDLE;

        // Tell the driver that initialization is complete.
        telemetry.addData("Status", "Initialized");
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit START
     */
    @Override
    public void init_loop() {
    }

    /*
     * Code to run ONCE when the driver hits START
     */
    @Override
    public void start() {
        runtime.reset();
        telemetry.addData("Status", "Started");
    }

    /*
     * Code to run REPEATEDLY after the driver hits START but before they hit STOP
     */
    @Override
    public void loop() {
        // Setup a variable for each drive wheel to save power level for telemetry
        double  drive           = 0.0;        // Desired forward power/speed (-1 to +1)
        double  strafe          = 0.0;        // Desired strafe power/speed (-1 to +1)
        double  turn            = 0.0;        // Desired turning power/speed (-1 to +1)

        // Choose to drive using either Tank Mode, or POV Mode
        // Comment out the method that's not used.  The default below is POV.

        // POV Mode uses left stick to go forward, and right stick to turn.
        // - This uses basic math to combine motions and is easier to drive straight.
        drive  = -gamepad1.left_stick_y;  // Reduce drive rate to 50%.
        strafe = -gamepad1.left_stick_x;  // Reduce strafe rate to 50%.
        turn   = -gamepad1.right_stick_x;  // Reduce turn rate to 33%.
        telemetry.addData("Manual","Drive %5.2f, Strafe %5.2f, Turn %5.2f ", drive, strafe, turn);

        // Tank Mode uses one stick to control each wheel.
        // - This requires no math, but it is hard to drive forward slowly and keep straight.
        // leftPower  = -gamepad1.left_stick_y ;
        // rightPower = -gamepad1.right_stick_y ;

        // Apply desired axes motions to the drivetrain.
        moveRobot(drive,strafe,turn);


        //catapult_process();
        // Show the elapsed game time and wheel power.

        /*
         * Here we give the driver control to
         * 1. toggle Elevator Servos up or down with Y,
         * 2. Intake On with launcher in reverse slow speed or Off with A
         */

        if (gamepad1.aWasPressed()){ //Intake On with launcher in reverse slow speed or Off with A
            switch (intakeState){
                case ON:
                    intakeState = IntakeState.OFF;
                    intake.setPower(0);
                    launcher.setVelocity(STOP_SPEED);
                    rearBoot.setPosition(RRBOOTGATE_POSITION);
                    leftSideIntake.setPower(STOP_SPEED);
                    rightSideIntake.setPower(STOP_SPEED);
                    break;
                case OFF:
                    intakeState = IntakeState.ON;
                    intake.setPower(1);
                    launcher.setVelocity(LAUNCHER_REVERSE_TARGET_VELOCITY);
                    leftSideIntake.setPower(FULL_SPEED);
                    rightSideIntake.setPower(FULL_SPEED);
                    break;
            }
        }

        /*
         * Here we give the operator control to
         * 1. start the launcher, front feeder and intake with Y
         * 2. stop the front feeder and reverse the launcher at slow speed with B to get ready for intake,
         * 3. reverse the launcher at full speed with A to unstuck the launcher,
         * 4. reverse the intake with X to spit out extra artifacts
         */
        if (gamepad2.y) { // start the launcher, front feeder and intake with Y
            launcher.setVelocity(launcherTarget);
            //frontFeeder.setPower(-FULL_SPEED);
            intakeState = IntakeState.ON;
            intake.setPower(1);

            if (launcher.getVelocity() < 200.0){
                lightServo.setPosition(0.2); // Low1 brightness
            }

        } else if (gamepad2.b) { // reverse launcher at slow speed
            launcher.setVelocity(LAUNCHER_REVERSE_TARGET_VELOCITY);
            intakeState = IntakeState.ON;
            intake.setPower(1);
        } else if (gamepad2.a) { // Intake On with launcher in reverse slow speed or Off with A
            switch (intakeState){
                case ON:
                    intakeState = IntakeState.OFF;
                    intake.setPower(0);
                    launcher.setVelocity(STOP_SPEED);
                    leftSideIntake.setPower(STOP_SPEED);
                    rightSideIntake.setPower(STOP_SPEED);
                    break;
                case OFF:
                    intakeState = IntakeState.ON;
                    intake.setPower(1);
                    launcher.setVelocity(LAUNCHER_REVERSE_TARGET_VELOCITY);
                    leftSideIntake.setPower(FULL_SPEED);
                    rightSideIntake.setPower(FULL_SPEED);
                    break;
            }
        } else if (gamepad2.x) { // reverse the intake and launcher to spit out excess intake
            launcher.setVelocity(LAUNCHER_REVERSE_TARGET_VELOCITY);
            intakeState = IntakeState.OFF;
            intake.setPower(-1);
        }

        /*
         * Here we give the driver control to pickup Nector and Pollen and drop it into the Flower. 1. Dpad Up for a Close Shot, 2. Dpad Down for a Far Shot
         */
        if (gamepad1.dpadUpWasPressed()) { //Dpad Up for moving the elevator up
            flowerElevator.setTargetPosition(1500);
            flowerElevator.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            flowerArm.setPosition(0.1);
            flowerWrist.setPosition(0.3);
            flowerElevator.setPower(0.8);
        } else if (gamepad1.dpadDownWasPressed()) { //Dpad Down for positioning to fecth Nector or Pollen
            flowerArm.setPosition(0.0);
            flowerWrist.setPosition(0.0);
            flowerElevator.setTargetPosition(0);
            flowerElevator.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            flowerElevator.setPower(0.8);
        } else if (gamepad1.dpadLeftWasPressed()) { //Dpad Left for fetching Nector and Pollen
            flowerArm.setPosition(0.0);
            flowerWrist.setPosition(0.0);
            flowerElevator.setTargetPosition(0);
            flowerElevator.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            flowerElevator.setPower(0.8);
        } else if (gamepad1.dpadRightWasPressed()) { //Dpad Right for dropping Nector and Pollen into the Flower
            flowerElevator.setTargetPosition(1);
            flowerElevator.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            flowerElevator.setPower(0.8);
            flowerArm.setPosition(0.1);
            flowerWrist.setPosition(1.2);
        }

        /*
         * Here we give the Operator control of the speed of the launcher motor without automatically
         * queuing a shot. 1. Dpad Up for a Close Shot, 2. Dpad Down for a Far Shot
         */
        if (gamepad2.dpadUpWasPressed()) { //Dpad Up for a Close Shot
            launcherDistance = LauncherDistance.CLOSE;
            launcherTarget = LAUNCHER_CLOSE_TARGET_VELOCITY;
            launcherMin = LAUNCHER_CLOSE_MIN_VELOCITY;
        } else if (gamepad2.dpadDownWasPressed()) { //Dpad Down for a Far Shot
            launcherDistance = LauncherDistance.FAR;
            launcherTarget = LAUNCHER_FAR_TARGET_VELOCITY;
            launcherMin = LAUNCHER_FAR_MIN_VELOCITY;
        } else if (gamepad2.dpadLeftWasPressed()) { //Dpad Left Slow Down Launcher Speed
            launcherTarget -= 100;
            launcherMin -= 100;
        } else if (gamepad2.dpadRightWasPressed()) { //Dpad Right Speed Up Launcher
            launcherTarget += 100;
            launcherMin += 100;
        }

        launchLeft(gamepad2.leftBumperWasPressed()); //Shooting with the left bumper, 4 shots time based
        launchRight(gamepad2.rightBumperWasPressed()); // Shooting with the right bumper, 1 shot time based

        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Motors", "drive (%.2f), strafe (%.2f), turn (%.2f)", drive, strafe, turn);
        telemetry.addData("launch distance", launcherDistance);
        telemetry.addData("Launcher Velocity", launcher.getVelocity());
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        //Stop the Launcher
        launcher.setPower (0.0);
    }


    /**
     * Move robot according to desired axes motions
     * <p>
     * Positive X is forward
     * <p>
     * Positive Y is strafe left
     * <p>
     * Positive Yaw is counter-clockwise
     */
    public void moveRobot(double x, double y, double yaw) {
        // Calculate wheel powers.
        double frontLeftPower    =  x - y - yaw;
        double frontRightPower   =  x + y + yaw;
        double backLeftPower     =  x + y - yaw;
        double backRightPower    =  x - y + yaw;

        // Normalize wheel powers to be less than 1.0
        double max = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
        max = Math.max(max, Math.abs(backLeftPower));
        max = Math.max(max, Math.abs(backRightPower));

        if (max > 1.0) {
            frontLeftPower /= max;
            frontRightPower /= max;
            backLeftPower /= max;
            backRightPower /= max;
        }

        // Send powers to the wheels.
        leftFrontDrive.setPower(frontLeftPower);
        rightFrontDrive.setPower(frontRightPower);
        leftBackDrive.setPower(backLeftPower);
        rightBackDrive.setPower(backRightPower);
    }


    //Shooting with the left bumper, 4 shots time based
    //
    void launchLeft(boolean shotRequested) {
        switch (leftLaunchState) {
            case IDLE:
                if (shotRequested) {
                    leftLaunchState = LaunchState.BOOTOPEN;
                    bootOpenTimer.reset();
                }
                break;
            case BOOTOPEN:
                if (bootOpenTimer.seconds() > BOOTOPEN_TIME_SECONDS) {
                    rearBootState = RearBootState.RRBOOTOPEN;
                    rearBoot.setPosition(RRBOOTOPEN_POSITION);
                    leftLaunchState = LaunchState.BOOTCLOSE;
                }
                bootCloseTimer.reset();
                break;
            case BOOTCLOSE:
                if(bootCloseTimer.seconds() > BOOTCLOSE_TIME_SECONDS){
                    rearBootState = RearBootState.RRBOOTCLOSE;
                    rearBoot.setPosition(RRBOOTCLOSE_POSITION);
                    leftLaunchState = LaunchState.BOOTOPEN2;

                }
                bootOpenTimer.reset();
                break;
            case BOOTOPEN2:
                if (bootOpenTimer.seconds() > BOOTOPEN_TIME_SECONDS) {
                    rearBootState = RearBootState.RRBOOTOPEN;
                    rearBoot.setPosition(RRBOOTOPEN_POSITION);
                    leftLaunchState = LaunchState.BOOTCLOSE2;
                    bootCloseTimer.reset();
                }
                break;
            case BOOTCLOSE2:
                if(bootCloseTimer.seconds() > BOOTCLOSE_TIME_SECONDS){
                    rearBootState = RearBootState.RRBOOTCLOSE;
                    rearBoot.setPosition(RRBOOTCLOSE_POSITION);
                    leftLaunchState = LaunchState.BOOTOPEN3;
                    bootOpenTimer.reset();
                }
                break;
            case BOOTOPEN3:
                if (bootOpenTimer.seconds() > BOOTOPEN_TIME_SECONDS) {
                    rearBootState = RearBootState.RRBOOTOPEN;
                    rearBoot.setPosition(RRBOOTOPEN_POSITION);
                    leftLaunchState = LaunchState.BOOTCLOSE3;
                    bootCloseTimer.reset();
                }
                break;
            case BOOTCLOSE3:
                if(bootCloseTimer.seconds() > BOOTCLOSE_TIME_SECONDS){
                    rearBootState = RearBootState.RRBOOTCLOSE;
                    rearBoot.setPosition(RRBOOTCLOSE_POSITION);
                    leftLaunchState = LaunchState.BOOTOPEN4;
                    bootOpenTimer.reset();
                }
                break;
            case BOOTOPEN4:
                if (bootOpenTimer.seconds() > BOOTOPEN_TIME_SECONDS) {
                    rearBootState = RearBootState.RRBOOTOPEN;
                    rearBoot.setPosition(RRBOOTOPEN_POSITION);
                    leftLaunchState = LaunchState.BOOTCLOSE4;
                    bootCloseTimer.reset();
                }
                break;
            case BOOTCLOSE4:
                if(bootCloseTimer.seconds() > BOOTCLOSE_TIME_SECONDS){
                    rearBootState = RearBootState.RRBOOTCLOSE;
                    rearBoot.setPosition(RRBOOTCLOSE_POSITION);
                    leftLaunchState = LaunchState.BOOTOPEN5;
                    bootOpenTimer.reset();
                }
                break;
            case BOOTOPEN5:
                if (bootOpenTimer.seconds() > BOOTOPEN_TIME_SECONDS) {
                    rearBootState = RearBootState.RRBOOTOPEN;
                    rearBoot.setPosition(RRBOOTGATE_POSITION);
                    leftLaunchState = LaunchState.IDLE;
                    bootCloseTimer.reset();
                }
                break;
        }
    }


    // Shooting with the right bumper, 1 shot time based
    //
    void launchRight(boolean shotRequested) {
        switch (rightLaunchState) {
            case IDLE:
                if (shotRequested) {
                    rightLaunchState = LaunchState.BOOTOPEN;
                    bootOpenTimer.reset();
                }
                break;
            case BOOTOPEN:
                if (bootOpenTimer.seconds() > BOOTOPEN_TIME_SECONDS) {
                    rearBootState = RearBootState.RRBOOTOPEN;
                    rearBoot.setPosition(RRBOOTOPEN_POSITION);
                    rightLaunchState = LaunchState.BOOTCLOSE;
                }
                bootCloseTimer.reset();
                break;
            case BOOTCLOSE:
                if(bootCloseTimer.seconds() > BOOTCLOSE_TIME_SECONDS){
                    rearBootState = RearBootState.RRBOOTCLOSE;
                    rearBoot.setPosition(RRBOOTCLOSE_POSITION);
                    rightLaunchState = LaunchState.BOOTOPEN2;
                }
                bootOpenTimer.reset();
                break;
            case BOOTOPEN2:
                if (bootOpenTimer.seconds() > BOOTOPEN_TIME_SECONDS) {
                    rearBootState = RearBootState.RRBOOTOPEN;
                    rearBoot.setPosition(RRBOOTGATE_POSITION);
                    rightLaunchState = LaunchState.IDLE;
                    bootCloseTimer.reset();
                }
                break;
        }
    }

}