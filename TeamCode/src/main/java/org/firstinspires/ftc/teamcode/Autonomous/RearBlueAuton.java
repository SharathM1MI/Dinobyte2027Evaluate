package org.firstinspires.ftc.teamcode.Autonomous;


import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

@Autonomous(name = "Rear Blue Auton")
public class RearBlueAuton extends OpMode {

    DcMotorEx launcher;

    Servo leftGate, rightGate, rearFeeder, frontFeeder;
    private Follower follower;
    private Timer pathTimer, actionTimer, opmodeTimer, shootingTimer;

    private int pathState;

    private final Pose startPose = new Pose(36, 9, Math.toRadians(90));
    private final Pose shoot1Pose = new Pose(64, 15, Math.toRadians(115));

    private final Pose humanPlayerPose = new Pose(16,12, Math.toRadians(90));

    private Path scorePreload;

    private Path intakeFromHuman;

    private Path humanToScore;

    public void autonomousPathUpdate() {
        switch(pathState) {
            //score the preload
            case 0:
                follower.followPath(scorePreload);
                launcher.setPower(-0.7);
                setPathState(100);
                break;

            //shoot the balls

            case 100:
                if(!follower.isBusy()) {
                    shootBalls();
                    setPathState(101);
                }
                break;

            //start using servo

            case 101:
                if (pathTimer.getElapsedTimeSeconds()>2.5) {
                    leftGate.setPosition(-0.8);
                    rightGate.setPosition(0.45);
                    setPathState(102);
                }
                break;

            case 102:
                if (pathTimer.getElapsedTimeSeconds()>0.2) {
                    leftGate.setPosition(0.55);
                    rightGate.setPosition(-0.6);
                    setPathState(103);
                }
                break;

            case 103:
                if (pathTimer.getElapsedTimeSeconds()>0.5) {
                    leftGate.setPosition(-0.8);
                    rightGate.setPosition(0.45);
                    setPathState(104);
                }
                break;

            case 104:
                if (pathTimer.getElapsedTimeSeconds()>0.2) {
                    leftGate.setPosition(0.55);
                    rightGate.setPosition(-0.6);
                    setPathState(105);
                }
                break;

            case 105:
                if (pathTimer.getElapsedTimeSeconds()>0.5) {
                    leftGate.setPosition(-0.8);
                    rightGate.setPosition(0.45);
                    setPathState(106);
                }
                break;

            case 106:
                if (pathTimer.getElapsedTimeSeconds()>0.2) {
                    leftGate.setPosition(0.55);
                    rightGate.setPosition(-0.6);
                    setPathState(107);
                }
                break;

            case 107:
                if (pathTimer.getElapsedTimeSeconds()>0.5) {
                    leftGate.setPosition(-0.8);
                    rightGate.setPosition(0.45);
                    setPathState(108);
                }
                break;

            case 108:
                if (pathTimer.getElapsedTimeSeconds()>0.3) {
                    leftGate.setPosition(0.55);
                    rightGate.setPosition(-0.6);
                    setPathState(1);
                }
                break;

            //intake from human player



            case 1:
                if(pathTimer.getElapsedTimeSeconds()>2) {
                    leftGate.setPosition(-0.8);
                    rightGate.setPosition(0.45);
                    follower.followPath(intakeFromHuman);
                    setPathState(2);
                }
                break;

            //reset the cycle

            case 2:
                if(pathTimer.getElapsedTimeSeconds()>3) {
                    follower.followPath(humanToScore);
                    setPathState(101);
                }
        }
    }

    public void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
    }

    public void shootBalls() {
        launcher.setPower(-0.8);

        shootingTimer.resetTimer();

        leftGate.setPosition(0.55);
        rightGate.setPosition(-0.6);
        frontFeeder.setPosition(1.0);
        rearFeeder.setPosition(0.2);


        while(shootingTimer.getElapsedTimeSeconds()<0.2) {
            leftGate.setPosition(-0.8);
            rightGate.setPosition(0.45);

        }

        shootingTimer.resetTimer();

        while(shootingTimer.getElapsedTimeSeconds()<0.3) {
            leftGate.setPosition(0.55);
            rightGate.setPosition(-0.6);

        }

    }

    public void buildPaths() {
        scorePreload = new Path(new BezierLine(startPose, shoot1Pose));
        scorePreload.setLinearHeadingInterpolation(startPose.getHeading(), shoot1Pose.getHeading());

        intakeFromHuman = new Path(new BezierLine(shoot1Pose, humanPlayerPose));
        intakeFromHuman.setLinearHeadingInterpolation(shoot1Pose.getHeading(), humanPlayerPose.getHeading());

        humanToScore = new Path(new BezierLine(humanPlayerPose, shoot1Pose));
        humanToScore.setLinearHeadingInterpolation(humanPlayerPose.getHeading(), shoot1Pose.getHeading());
    }

    @Override
    public void loop() {
        follower.update();
        autonomousPathUpdate();

        telemetry.addData("path state", pathState);
        telemetry.update();

    }
    @Override
    public void init() {
        pathTimer = new Timer();
        opmodeTimer = new Timer();
        shootingTimer = new Timer();
        opmodeTimer.resetTimer();

        follower = Constants.createFollower(hardwareMap);
        buildPaths();
        follower.setStartingPose(startPose);

        launcher = hardwareMap.get(DcMotorEx.class, "launcher");

        leftGate = hardwareMap.get(Servo.class, "leftGate");
        rightGate = hardwareMap.get(Servo.class, "rightGate");
        rearFeeder = hardwareMap.get(Servo.class, "rearFeeder");
        frontFeeder = hardwareMap.get(Servo.class, "frontFeeder");

        leftGate.setPosition(-0.8);
        rightGate.setPosition(0.45);

    }

    @Override
    public void init_loop() {}

    @Override
    public void start() {
        opmodeTimer.resetTimer();

    }

    @Override
    public void stop() {

    }
}
