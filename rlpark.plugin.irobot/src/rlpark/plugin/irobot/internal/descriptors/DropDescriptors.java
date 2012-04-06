package rlpark.plugin.irobot.internal.descriptors;

import rlpark.plugin.irobot.data.IRobots;
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
    DropData[] descriptors = new DropData[] { new DropEndBit("Unknown"), new DropBit(IRobots.WheelDropCaster, 4),
        new DropBit(IRobots.WheelDropLeft, 3), new DropBit(IRobots.WheelDropRight, 2), new DropBit(IRobots.BumpLeft, 1),
        new DropBit(IRobots.BumpRight, 0), new DropEndBit("EndPacket7"), new DropBooleanBit(IRobots.WallSensor),
        new DropBooleanBit(IRobots.CliffSensorLeft), new DropBooleanBit(IRobots.CliffSensorFrontLeft),
        new DropBooleanBit(IRobots.CliffSensorFrontRight), new DropBooleanBit(IRobots.CliffSensorRight),
        new DropBooleanBit(IRobots.WallVirtual), new DropBit(IRobots.LowSideDriverOverCurrent + 0, 0),
        new DropBit(IRobots.LowSideDriverOverCurrent + 1, 1), new DropBit(IRobots.LowSideDriverOverCurrent + 2, 2),
        new DropBit(IRobots.WheelOverCurrentRight, 3), new DropBit(IRobots.WheelOverCurrentLeft, 4), new DropEndBit("EndPacketID14"),
        new DropEndBit("Unused01"), new DropEndBit("Unused02"), new DropByteUnsigned(IRobots.ICOmni),
        new DropBit(IRobots.ButtonAdvance, 2), new DropBit(IRobots.ButtonPlay, 0), new DropEndBit("EndPacket18"),
        new DropShortSigned(IRobots.DriveDistance), new DropShortSigned(IRobots.DriveAngle), new DropByteUnsigned(IRobots.ChargingState),
        new DropShortUnsigned(IRobots.BatteryVoltage), new DropShortSigned(IRobots.BatteryCurrent),
        new DropByteSigned(IRobots.BatteryTemperature), new DropShortUnsigned(IRobots.BatteryCharge),
        new DropShortUnsigned(IRobots.BatteryCapacity), new DropShortUnsigned(IRobots.WallSignal),
        new DropShortUnsigned(IRobots.CliffSignalLeft), new DropShortUnsigned(IRobots.CliffSignalFrontLeft),
        new DropShortUnsigned(IRobots.CliffSignalFrontRight), new DropShortUnsigned(IRobots.CliffSignalRight),
        new DropByteUnsigned(IRobots.CargoBayDigitalInputs), new DropShortUnsigned(IRobots.CargoBayAnalogSignal),
        new DropBit(IRobots.ConnectedHomeBase, 1), new DropBit(IRobots.ConnectedInternalCharger, 0), new DropEndBit("EndPacket34"),
        new DropByteUnsigned(IRobots.OIMode), new DropByteUnsigned(IRobots.SongNumber), new DropBooleanBit(IRobots.SongPlaying),
        new DropByteUnsigned(IRobots.NumberStreamPackets), new DropShortSigned(IRobots.DriverRequestedVelocity),
        new DropShortSigned(IRobots.DriverRequestedRadius), new DropShortSigned(IRobots.WheelRequestedVelocityRight),
        new DropShortSigned(IRobots.WheelRequestedVelocityLeft), };
    return createDrop(IRobots.CreateSensorDropName, descriptors, IRobots.CreateSensorsPacketSize);
  }

  public static Drop newCommandSerialDrop() {
    return new Drop(IRobots.IRobotCommandDropName, new DropByteArray("CommandData", 36));
  }

  public static Drop newSensorSerialDrop(String name, int dataSize) {
    return new Drop(name, new DropByteArray("SensorSensor", dataSize));
  }

  public static Drop newRoombaSensorDrop() {
    DropData[] descriptors = new DropData[] { new DropBit(IRobots.WheelDropLeft, 3), new DropBit(IRobots.WheelDropRight, 2),
        new DropBit(IRobots.BumpLeft, 1), new DropBit(IRobots.BumpRight, 0), new DropEndBit("EndPacket7"),
        new DropBooleanBit(IRobots.WallSensor), new DropBooleanBit(IRobots.CliffSensorLeft), new DropBooleanBit(IRobots.CliffSensorFrontLeft),
        new DropBooleanBit(IRobots.CliffSensorFrontRight), new DropBooleanBit(IRobots.CliffSensorRight),
        new DropBooleanBit(IRobots.WallVirtual), new DropByteUnsigned(IRobots.DirtDetect), new DropByteUnsigned(IRobots.ICOmni),
        new DropByteUnsigned(IRobots.ICLeft), new DropByteUnsigned(IRobots.ICRight), new DropBit(IRobots.ButtonClock, 7),
        new DropBit(IRobots.ButtonSchedule, 6), new DropBit(IRobots.ButtonDay, 5), new DropBit(IRobots.ButtonHour, 4),
        new DropBit(IRobots.ButtonMinute, 3), new DropBit(IRobots.ButtonDock, 2), new DropBit(IRobots.ButtonSpot, 1),
        new DropBit(IRobots.ButtonClean, 0), new DropEndBit("EndPacket18"), new DropShortSigned(IRobots.DriveDistance),
        new DropShortSigned(IRobots.DriveAngle), new DropByteUnsigned(IRobots.ChargingState), new DropShortUnsigned(IRobots.BatteryVoltage),
        new DropShortSigned(IRobots.BatteryCurrent), new DropByteSigned(IRobots.BatteryTemperature),
        new DropShortUnsigned(IRobots.BatteryCharge), new DropShortUnsigned(IRobots.BatteryCapacity),
        new DropShortUnsigned(IRobots.WallSignal), new DropShortUnsigned(IRobots.CliffSignalLeft),
        new DropShortUnsigned(IRobots.CliffSignalFrontLeft), new DropShortUnsigned(IRobots.CliffSignalFrontRight),
        new DropShortUnsigned(IRobots.CliffSignalRight), new DropByteArray("Unused", 3), new DropBit(IRobots.ConnectedHomeBase, 1),
        new DropBit(IRobots.ConnectedInternalCharger, 0), new DropEndBit("EndPacket34"), new DropByteUnsigned(IRobots.OIMode),
        new DropByteUnsigned(IRobots.SongNumber), new DropBooleanBit(IRobots.SongPlaying), new DropByteUnsigned(IRobots.NumberStreamPackets),
        new DropShortSigned(IRobots.DriverRequestedVelocity), new DropShortSigned(IRobots.DriverRequestedRadius),
        new DropShortSigned(IRobots.WheelRequestedVelocityRight), new DropShortSigned(IRobots.WheelRequestedVelocityLeft),
        new DropShortUnsigned(IRobots.WheelEncoderRight), new DropShortUnsigned(IRobots.WheelEncoderLeft),
        new DropBit(IRobots.LightBumpSensorRight, 5), new DropBit(IRobots.LightBumpSensorFrontRight, 4),
        new DropBit(IRobots.LightBumpSensorCenterRight, 3), new DropBit(IRobots.LightBumpSensorCenterLeft, 2),
        new DropBit(IRobots.LightBumpSensorFrontLeft, 1), new DropBit(IRobots.LightBumpSensorLeft, 0), new DropEndBit("EndPacket45"),
        new DropShortUnsigned(IRobots.LightBumpSignalLeft), new DropShortUnsigned(IRobots.LightBumpSignalFrontLeft),
        new DropShortUnsigned(IRobots.LightBumpSignalCenterLeft), new DropShortUnsigned(IRobots.LightBumpSignalCenterRight),
        new DropShortUnsigned(IRobots.LightBumpSignalFrontRight), new DropShortUnsigned(IRobots.LightBumpSignalRight),
        new DropShortSigned(IRobots.WheelMotorCurrentLeft), new DropShortSigned(IRobots.WheelMotorCurrentRight),
        new DropShortSigned(IRobots.MotorCurrentMainBrush), new DropShortSigned(IRobots.MotorCurrentSideBrush),
        new DropBooleanBit(IRobots.Stasis), new DropByteArray("Unused", 2) };
    return createDrop(IRobots.RoombaSensorDropName, descriptors, IRobots.RoombaSensorsPacketSize);
  }

}
