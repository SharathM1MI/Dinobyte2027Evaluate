package org.firstinspires.ftc.teamcode.Autonomous;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.TeleOp1.Intake;
import org.firstinspires.ftc.teamcode.TeleOp1.Outtake;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

@Autonomous(name = "RearRedAuton2Ball")
public class RearRedAuton2Ball extends OpMode {

    Intake intake;

    Outtake outtake;

    Follower follower;

    private Timer pathTimer, actionTimer, opmodeTimer, shootingTimer;

    private int pathState;

    private final Pose startPose = new Pose(39, 9, Math.toRadians(90));
    private final Pose shoot1Pose = new Pose(56, 15, Math.toRadians(60));

    private final Pose parkPose = new Pose(42, 36, Math.toRadians(180));

    private Path score;

    private Path getReadyforTele;

    public void autonomousPathUpdate() {
        switch(pathState) {
            //score the preload
            case 0:
                follower.followPath(score);
                setPathState(-1);
                break;



            //shoot the balls
        }
    }

    public void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
    }

    public void buildPaths() {
        score = new Path(new BezierLine(startPose, parkPose));
        score.setLinearHeadingInterpolation(startPose.getHeading(), parkPose.getHeading());


    }



    @Override
    public void init() {

        pathTimer = new Timer();
        opmodeTimer = new Timer();
        shootingTimer = new Timer();
        opmodeTimer.resetTimer();

        buildPaths();

        intake = new Intake(hardwareMap);
        outtake = new Outtake(hardwareMap);
        follower = Constants.createFollower(hardwareMap);

        follower.setStartingPose(startPose);

        outtake.closeGates();




    }

    @Override
    public void loop() {
        follower.update();
        autonomousPathUpdate();

        telemetry.addData("path state", pathState);
        telemetry.update();
    }

    @Override
    public void start() {
        opmodeTimer.resetTimer();
    }
}
