package org.firstinspires.ftc.teamcode.pedroPathing;

import com.pedropathing.control.FilteredPIDFCoefficients;
import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.Encoder;
import com.pedropathing.ftc.localization.constants.ThreeWheelConstants;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Constants {
    public static FollowerConstants followerConstants = new FollowerConstants()
            .mass(11.88412)
            .forwardZeroPowerAcceleration(-23.07)
            .lateralZeroPowerAcceleration(-198.167)
            .translationalPIDFCoefficients(new PIDFCoefficients(0.2, 0, 0.005, 0.03))
            .headingPIDFCoefficients(new PIDFCoefficients(1, 0, 0.1, 0.025))
            .drivePIDFCoefficients(new FilteredPIDFCoefficients(0.02, 0, 0.0002, 0.3, 0.01))
            .centripetalScaling(0.0004);

    /*

    Other info there is no mapping
    Yes. Your Foresight snippet contains: c.naturalForwardDeceleration.set(23.07) and
    c.naturalStrafeDeceleration.set(198.15).
    To use them in your old code, you would make them negative: .
    forwardZeroPowerAcceleration(-23.07) and .lateralZeroPowerAcceleration(-198.15


     */

/*
    public static ThreeWheelConfig localizerConfig = new ThreeWheelConfig(c -> {
        c.leftEncoderName.set("leftFrontDrive");
        c.rightEncoderName.set("rightFrontDrive");
        c.strafeEncoderName.set("leftBackDrive");
        c.imuName.set("imu");
        c.imu.set(new RevHubIMU(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.BACKWARD,
                RevHubOrientationOnRobot.UsbFacingDirection.LEFT
        )));

        c.leftPodY.set(3.777505319081218);
        c.rightPodY.set(-4.628353639901699);
        c.strafePodX.set(-5.511595592350807);
        c.forwardTicksToInches.set(0.002930941703957012);
        c.strafeTicksToInches.set(0.007890632198383773);
        c.turnTicksToRadians.set(0.0024611169387374287);
        c.leftEncoderDirection.set(Encoder.FORWARD);
        c.rightEncoderDirection.set(Encoder.REVERSE);
        c.strafeEncoderDirection.set(Encoder.FORWARD);

    });
    //cal from 10/28/2026
*/





    public static MecanumConstants driveConstants = new MecanumConstants()
            .maxPower(1)
            .rightFrontMotorName("rightFrontDrive")
            .rightRearMotorName("rightBackDrive")
            .leftRearMotorName("leftBackDrive")
            .leftFrontMotorName("leftFrontDrive")
            .leftFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
            .leftRearMotorDirection(DcMotorSimple.Direction.FORWARD)
            .rightFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
            .rightRearMotorDirection(DcMotorSimple.Direction.REVERSE)
            .xVelocity(28.0824)
            .yVelocity(165.5199);
/* indicated xvelocity and yvelocity can be derived from foresight parameters
                c.maxAchievableForwardVelocity.set(28.08242151384575);
                c.maxAchievableStrafeVelocity.set(165.5199207566869);*/

    public static ThreeWheelConstants localizerConstants = new ThreeWheelConstants()
            .turnTicksToInches(0.004922233877474857)  // calculatd from turnticks to radian with 2 inch radius
            .leftPodY(3.777) // from foresight
            .rightPodY(-4.628) //from foresight
            .strafePodX(-5.51159) //from foresight
            .leftEncoder_HardwareMapName("leftFrontDrive")
            .rightEncoder_HardwareMapName("rightFrontDrive")
            .strafeEncoder_HardwareMapName("rightBackDrive")
            .leftEncoderDirection(Encoder.FORWARD)
            .rightEncoderDirection(Encoder.REVERSE)
            .strafeEncoderDirection(Encoder.FORWARD)
            .strafeTicksToInches(0.007890632198383773) // from three wheel IMU cals
            .forwardTicksToInches(0.002930941703957012); //from three wheel IMU cal


    public static PathConstraints pathConstraints = new PathConstraints(0.99, 100, 1.35, 1);

    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(followerConstants, hardwareMap)
                .threeWheelLocalizer(localizerConstants)
                .pathConstraints(pathConstraints)
                .mecanumDrivetrain(driveConstants)


                .build();
    }
}
