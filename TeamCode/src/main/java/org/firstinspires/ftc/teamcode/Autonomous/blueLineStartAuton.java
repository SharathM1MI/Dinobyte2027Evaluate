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

import org.firstinspires.ftc.teamcode.TeleOp1.Intake;
import org.firstinspires.ftc.teamcode.TeleOp1.Outtake;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;


@Autonomous(name = "blueLineStartAuton")
public class blueLineStartAuton extends OpMode {
    Intake intake;

    Outtake outtake;



    DcMotorEx launcher;

    Servo leftGate, rightGate, rearFeeder, frontFeeder;
    private Follower follower;
    private Timer pathTimer, actionTimer, opmodeTimer, shootingTimer;

    private int pathState;

    private final Pose startPose = new Pose(24, 125.8, Math.toRadians(325));
    private final Pose readyToIntake = new Pose(42.6, 84, Math.toRadians(180));

    private final Pose moveToIntake = new Pose(20.5, 84, Math.toRadians(180));

    private final Pose moveToCloseScore = new Pose(59, 84, Math.toRadians(135));

    private final Pose getReadyForTeleOp = new Pose(47.9,134, Math.toRadians(270));



    private Path scorePreload;

    private Path readyToIntakeFirst;

    private Path moveToIntakeFirst;

    private Path moveToCloseScore1;

    private Path getReadyForTele;

    public void autonomousPathUpdate() {
        switch(pathState) {
            //score the preload
            case 0:
                follower.followPath(scorePreload);
                outtake.ShootBallLoop();
                setPathState(1);
                break;

            //start shooting sequence

            case 1:
                if(pathTimer.getElapsedTimeSeconds()>1) {
                    intake.spinIntake();
                    outtake.runFeeder();
                    outtake.openLeftGate();
                    setPathState(2);
                }
                break;

            //shoot first ball

            case 2:
                if (pathTimer.getElapsedTimeSeconds()>2) {
                    outtake.closeBoot();
                    setPathState(3);
                }
                break;

            //start shooting sequence

            case 3:
                if (pathTimer.getElapsedTimeSeconds()>1) {
                    outtake.openBoot();
                    outtake.closeLeftGate();
                    outtake.closeRightGate();
                    setPathState(4);

                }
                break;


            case 4:
                if (pathTimer.getElapsedTimeSeconds()>1) {
                    outtake.openRightGate();
                    outtake.openLeftGate();
                    setPathState(5);
                }
                break;

            //shoot second ball

            case 5:
                if (pathTimer.getElapsedTimeSeconds()>1.75) {
                    outtake.closeBoot();
                    setPathState(6);
                }
                break;

            case 6:
                if (pathTimer.getElapsedTimeSeconds()>1) {
                    outtake.closeRightGate();
                    outtake.closeLeftGate();
                    outtake.openBoot();
                    follower.followPath(readyToIntakeFirst);
                    intake.spinIntake();
                    setPathState(7);
                }
                break;

            case 7:
                if (pathTimer.getElapsedTimeSeconds()>2) {
                    follower.setMaxPower(0.3);
                    follower.followPath(moveToIntakeFirst);
                    setPathState(8);
                }
                break;

            case 8:
                if(pathTimer.getElapsedTimeSeconds()>1.5) {
                    follower.setMaxPower(1);
                    follower.followPath(moveToCloseScore1);
                    setPathState(9);
                }
                break;


            case 9:
                if(pathTimer.getElapsedTimeSeconds()>1.5) {
                    intake.spinIntake();
                    outtake.runFeeder();
                    outtake.openLeftGate();
                    setPathState(10);
                }
                break;

            case 10:
                if (pathTimer.getElapsedTimeSeconds()>1) {
                    outtake.closeBoot();
                    setPathState(11);
                }
                break;

            case 11:
                if (pathTimer.getElapsedTimeSeconds()>1) {
                    outtake.openBoot();
                    outtake.closeLeftGate();
                    outtake.closeRightGate();
                    setPathState(12);

                }
                break;
            case 12:
                if (pathTimer.getElapsedTimeSeconds()>1) {
                    outtake.openRightGate();
                    outtake.openLeftGate();
                    setPathState(13);
                }
                break;

            case 13:
                if (pathTimer.getElapsedTimeSeconds()>2) {
                    outtake.closeBoot();
                    setPathState(14);
                }
                break;

            case 14:
                if (pathTimer.getElapsedTimeSeconds()>2) {
                    outtake.closeRightGate();
                    outtake.closeLeftGate();
                    outtake.openBoot();
                    follower.followPath(getReadyForTele);
                    setPathState(15);
                }
        }
    }

    public void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
    }



    public void buildPaths() {
        scorePreload = new Path(new BezierLine(startPose, moveToCloseScore));
        scorePreload.setLinearHeadingInterpolation(startPose.getHeading(), moveToCloseScore.getHeading());

        readyToIntakeFirst = new Path(new BezierLine(moveToCloseScore, readyToIntake));
        readyToIntakeFirst.setLinearHeadingInterpolation(moveToCloseScore.getHeading(), readyToIntake.getHeading());

        moveToIntakeFirst = new Path(new BezierLine(readyToIntake, moveToIntake));
        moveToIntakeFirst.setTangentHeadingInterpolation();
        // moveToIntakeFirst.setVelocityConstraint(0.1); (unnecessary?)

        moveToCloseScore1 = new Path(new BezierLine(moveToIntake, moveToCloseScore));
        moveToCloseScore1.setLinearHeadingInterpolation(moveToIntake.getHeading(), moveToCloseScore.getHeading());

        getReadyForTele = new Path(new BezierLine(moveToCloseScore, getReadyForTeleOp));
        getReadyForTele.setLinearHeadingInterpolation(moveToCloseScore.getHeading(), getReadyForTeleOp.getHeading());
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

        outtake = new Outtake(hardwareMap);
        intake = new Intake(hardwareMap);

        follower = Constants.createFollower(hardwareMap);
        buildPaths();
        follower.setStartingPose(startPose);

        launcher = hardwareMap.get(DcMotorEx.class, "launcher");

        leftGate = hardwareMap.get(Servo.class, "leftGate");
        rightGate = hardwareMap.get(Servo.class, "rightGate");

        outtake.closeGates();
        outtake.openBoot();

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

