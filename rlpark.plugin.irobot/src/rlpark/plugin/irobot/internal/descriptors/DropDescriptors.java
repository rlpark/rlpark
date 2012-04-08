package rlpark.plugin.irobot.internal.descriptors;

import rlpark.plugin.irobot.data.IRobotLabels;
import rlpark.plugin.robot.internal.disco.drops.Drop;
import rlpark.plugin.robot.internal.disco.drops.DropBit;
import rlpark.plugin.robot.internal.disco.drops.DropBooleanBit;
import rlpark.plugin.robot.internal.disco.drops.DropByteArray;
import rlpark.plugin.robot.internal.disco.drops.DropByteSigned;
import rlpark.plugin.robot.internal.disco.drops.DropByteUnsigned;
import rlpark.plugin.robot.internal.disco.drops.DropData;
import rlpark.plugin.robot.internal.disco.drops.DropEndBit;
import rlpark.plugin.robot.internal.disco.drops.DropShortSigned;
import rlpark.plugin.robot.internal.disco.drops.DropShortUnsigned;

public class DropDescriptors {

  public static Drop createDrop(String dropName, DropData[] descriptors, int requiredSize) {
    DropData[] completedDescriptors = descriptors;
    int parsedSize = 0;
    for (DropData dropData : descriptors)
      parsedSize += dropData.size();
    if (requiredSize - parsedSize != 0)
      throw new RuntimeException(String.format("Drop descriptors size (%d) does not match required size (%d).",
                                               parsedSize, requiredSize));
    return new Drop(dropName, completedDescriptors);
  }

  public static Drop newCreateSensorDrop() {
    DropData[] descriptors = new DropData[] { new DropEndBit("Unknown"), new DropBit(IRobotLabels.WheelDropCaster, 4),
        new DropBit(IRobotLabels.WheelDropLeft, 3), new DropBit(IRobotLabels.WheelDropRight, 2), new DropBit(IRobotLabels.BumpLeft, 1),
        new DropBit(IRobotLabels.BumpRight, 0), new DropEndBit("EndPacket7"), new DropBooleanBit(IRobotLabels.WallSensor),
        new DropBooleanBit(IRobotLabels.CliffSensorLeft), new DropBooleanBit(IRobotLabels.CliffSensorFrontLeft),
        new DropBooleanBit(IRobotLabels.CliffSensorFrontRight), new DropBooleanBit(IRobotLabels.CliffSensorRight),
        new DropBooleanBit(IRobotLabels.WallVirtual), new DropBit(IRobotLabels.LowSideDriverOverCurrent + 0, 0),
        new DropBit(IRobotLabels.LowSideDriverOverCurrent + 1, 1), new DropBit(IRobotLabels.LowSideDriverOverCurrent + 2, 2),
        new DropBit(IRobotLabels.WheelOverCurrentRight, 3), new DropBit(IRobotLabels.WheelOverCurrentLeft, 4), new DropEndBit("EndPacketID14"),
        new DropEndBit("Unused01"), new DropEndBit("Unused02"), new DropByteUnsigned(IRobotLabels.ICOmni),
        new DropBit(IRobotLabels.ButtonAdvance, 2), new DropBit(IRobotLabels.ButtonPlay, 0), new DropEndBit("EndPacket18"),
        new DropShortSigned(IRobotLabels.DriveDistance), new DropShortSigned(IRobotLabels.DriveAngle), new DropByteUnsigned(IRobotLabels.ChargingState),
        new DropShortUnsigned(IRobotLabels.BatteryVoltage), new DropShortSigned(IRobotLabels.BatteryCurrent),
        new DropByteSigned(IRobotLabels.BatteryTemperature), new DropShortUnsigned(IRobotLabels.BatteryCharge),
        new DropShortUnsigned(IRobotLabels.BatteryCapacity), new DropShortUnsigned(IRobotLabels.WallSignal),
        new DropShortUnsigned(IRobotLabels.CliffSignalLeft), new DropShortUnsigned(IRobotLabels.CliffSignalFrontLeft),
        new DropShortUnsigned(IRobotLabels.CliffSignalFrontRight), new DropShortUnsigned(IRobotLabels.CliffSignalRight),
        new DropByteUnsigned(IRobotLabels.CargoBayDigitalInputs), new DropShortUnsigned(IRobotLabels.CargoBayAnalogSignal),
        new DropBit(IRobotLabels.ConnectedHomeBase, 1), new DropBit(IRobotLabels.ConnectedInternalCharger, 0), new DropEndBit("EndPacket34"),
        new DropByteUnsigned(IRobotLabels.OIMode), new DropByteUnsigned(IRobotLabels.SongNumber), new DropBooleanBit(IRobotLabels.SongPlaying),
        new DropByteUnsigned(IRobotLabels.NumberStreamPackets), new DropShortSigned(IRobotLabels.DriverRequestedVelocity),
        new DropShortSigned(IRobotLabels.DriverRequestedRadius), new DropShortSigned(IRobotLabels.WheelRequestedVelocityRight),
        new DropShortSigned(IRobotLabels.WheelRequestedVelocityLeft), };
    return createDrop(IRobotLabels.CreateSensorDropName, descriptors, IRobotLabels.CreateSensorsPacketSize);
  }

  public static Drop newCommandSerialDrop() {
    return new Drop(IRobotLabels.IRobotCommandDropName, new DropByteArray("CommandData", 36));
  }

  public static Drop newSensorSerialDrop(String name, int dataSize) {
    return new Drop(name, new DropByteArray("SensorSensor", dataSize));
  }

  public static Drop newRoombaSensorDrop() {
    DropData[] descriptors = new DropData[] { new DropBit(IRobotLabels.WheelDropLeft, 3), new DropBit(IRobotLabels.WheelDropRight, 2),
        new DropBit(IRobotLabels.BumpLeft, 1), new DropBit(IRobotLabels.BumpRight, 0), new DropEndBit("EndPacket7"),
        new DropBooleanBit(IRobotLabels.WallSensor), new DropBooleanBit(IRobotLabels.CliffSensorLeft), new DropBooleanBit(IRobotLabels.CliffSensorFrontLeft),
        new DropBooleanBit(IRobotLabels.CliffSensorFrontRight), new DropBooleanBit(IRobotLabels.CliffSensorRight),
        new DropBooleanBit(IRobotLabels.WallVirtual), new DropByteUnsigned(IRobotLabels.DirtDetect), new DropByteUnsigned(IRobotLabels.ICOmni),
        new DropByteUnsigned(IRobotLabels.ICLeft), new DropByteUnsigned(IRobotLabels.ICRight), new DropBit(IRobotLabels.ButtonClock, 7),
        new DropBit(IRobotLabels.ButtonSchedule, 6), new DropBit(IRobotLabels.ButtonDay, 5), new DropBit(IRobotLabels.ButtonHour, 4),
        new DropBit(IRobotLabels.ButtonMinute, 3), new DropBit(IRobotLabels.ButtonDock, 2), new DropBit(IRobotLabels.ButtonSpot, 1),
        new DropBit(IRobotLabels.ButtonClean, 0), new DropEndBit("EndPacket18"), new DropShortSigned(IRobotLabels.DriveDistance),
        new DropShortSigned(IRobotLabels.DriveAngle), new DropByteUnsigned(IRobotLabels.ChargingState), new DropShortUnsigned(IRobotLabels.BatteryVoltage),
        new DropShortSigned(IRobotLabels.BatteryCurrent), new DropByteSigned(IRobotLabels.BatteryTemperature),
        new DropShortUnsigned(IRobotLabels.BatteryCharge), new DropShortUnsigned(IRobotLabels.BatteryCapacity),
        new DropShortUnsigned(IRobotLabels.WallSignal), new DropShortUnsigned(IRobotLabels.CliffSignalLeft),
        new DropShortUnsigned(IRobotLabels.CliffSignalFrontLeft), new DropShortUnsigned(IRobotLabels.CliffSignalFrontRight),
        new DropShortUnsigned(IRobotLabels.CliffSignalRight), new DropByteArray("Unused", 3), new DropBit(IRobotLabels.ConnectedHomeBase, 1),
        new DropBit(IRobotLabels.ConnectedInternalCharger, 0), new DropEndBit("EndPacket34"), new DropByteUnsigned(IRobotLabels.OIMode),
        new DropByteUnsigned(IRobotLabels.SongNumber), new DropBooleanBit(IRobotLabels.SongPlaying), new DropByteUnsigned(IRobotLabels.NumberStreamPackets),
        new DropShortSigned(IRobotLabels.DriverRequestedVelocity), new DropShortSigned(IRobotLabels.DriverRequestedRadius),
        new DropShortSigned(IRobotLabels.WheelRequestedVelocityRight), new DropShortSigned(IRobotLabels.WheelRequestedVelocityLeft),
        new DropShortUnsigned(IRobotLabels.WheelEncoderRight), new DropShortUnsigned(IRobotLabels.WheelEncoderLeft),
        new DropBit(IRobotLabels.LightBumpSensorRight, 5), new DropBit(IRobotLabels.LightBumpSensorFrontRight, 4),
        new DropBit(IRobotLabels.LightBumpSensorCenterRight, 3), new DropBit(IRobotLabels.LightBumpSensorCenterLeft, 2),
        new DropBit(IRobotLabels.LightBumpSensorFrontLeft, 1), new DropBit(IRobotLabels.LightBumpSensorLeft, 0), new DropEndBit("EndPacket45"),
        new DropShortUnsigned(IRobotLabels.LightBumpSignalLeft), new DropShortUnsigned(IRobotLabels.LightBumpSignalFrontLeft),
        new DropShortUnsigned(IRobotLabels.LightBumpSignalCenterLeft), new DropShortUnsigned(IRobotLabels.LightBumpSignalCenterRight),
        new DropShortUnsigned(IRobotLabels.LightBumpSignalFrontRight), new DropShortUnsigned(IRobotLabels.LightBumpSignalRight),
        new DropShortSigned(IRobotLabels.WheelMotorCurrentLeft), new DropShortSigned(IRobotLabels.WheelMotorCurrentRight),
        new DropShortSigned(IRobotLabels.MotorCurrentMainBrush), new DropShortSigned(IRobotLabels.MotorCurrentSideBrush),
        new DropBooleanBit(IRobotLabels.Stasis), new DropByteArray("Unused", 2) };
    return createDrop(IRobotLabels.RoombaSensorDropName, descriptors, IRobotLabels.RoombaSensorsPacketSize);
  }

}
