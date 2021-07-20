package akkamon.domain.iot;

import akka.actor.testkit.typed.javadsl.TestKitJunitResource;
import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;
import akkamon.domain.iot.DeviceManager.*;
import akkamon.domain.iot.DeviceManager.RespondAllTemperatures;
import akkamon.domain.iot.DeviceManager.Temperature;
import akkamon.domain.iot.DeviceManager.TemperatureReading;
import org.junit.ClassRule;
import org.junit.Test;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;

public class DeviceGroupQueryTest {

    @ClassRule
    public static final TestKitJunitResource testKit = new TestKitJunitResource();

    @Test
    public void testReturnTemperatureValueForWorkingDevices() {
        TestProbe<RespondAllTemperatures> requester =
            testKit.createTestProbe(RespondAllTemperatures.class);
        TestProbe<Device.Command> device1 = testKit.createTestProbe(Device.Command.class);
        TestProbe<Device.Command> device2 = testKit.createTestProbe(Device.Command.class);

        Map<String, ActorRef<Device.Command>> deviceIdToActor = new HashMap<>();
        deviceIdToActor.put("device1", device1.getRef());
        deviceIdToActor.put("device2", device2.getRef());

        ActorRef<DeviceGroupQuery.Command> queryActor =
            testKit.spawn(
                DeviceGroupQuery.create(
                    deviceIdToActor, 1L, requester.getRef(), Duration.ofSeconds(3)));

        device1.expectMessageClass(Device.ReadTemperature.class);
        device2.expectMessageClass(Device.ReadTemperature.class);

        queryActor.tell(
            new DeviceGroupQuery.WrappedRespondTemperature(
                new Device.RespondTemperature(0L, "device1", Optional.of(1.0))));

        queryActor.tell(
            new DeviceGroupQuery.WrappedRespondTemperature(
                new Device.RespondTemperature(0L, "device2", Optional.of(2.0))));

        RespondAllTemperatures response = requester.receiveMessage();
        assertEquals(1L, response.requestId);

        Map<String, TemperatureReading> expectedTemperatures = new HashMap<>();
        expectedTemperatures.put("device1", new Temperature(1.0));
        expectedTemperatures.put("device2", new Temperature(2.0));

        assertEquals(expectedTemperatures, response.temperatures);
    }

    @Test
    public void testReturnTemperatureNotAvailableForDevicesWithNoReadings() {
        TestProbe<RespondAllTemperatures> requester =
            testKit.createTestProbe(RespondAllTemperatures.class);
        TestProbe<Device.Command> device1 = testKit.createTestProbe(Device.Command.class);
        TestProbe<Device.Command> device2 = testKit.createTestProbe(Device.Command.class);

        Map<String, ActorRef<Device.Command>> deviceIdToActor = new HashMap<>();
        deviceIdToActor.put("device1", device1.getRef());
        deviceIdToActor.put("device2", device2.getRef());

        ActorRef<DeviceGroupQuery.Command> queryActor =
            testKit.spawn(
                DeviceGroupQuery.create(
                    deviceIdToActor, 1L, requester.getRef(), Duration.ofSeconds(3)));

        assertEquals(0L, device1.expectMessageClass(Device.ReadTemperature.class).requestId);
        assertEquals(0L, device2.expectMessageClass(Device.ReadTemperature.class).requestId);

        queryActor.tell(
            new DeviceGroupQuery.WrappedRespondTemperature(
                new Device.RespondTemperature(0L, "device1", Optional.empty())));

        queryActor.tell(
            new DeviceGroupQuery.WrappedRespondTemperature(
                new Device.RespondTemperature(0L, "device2", Optional.of(2.0))));

        RespondAllTemperatures response = requester.receiveMessage();
        assertEquals(1L, response.requestId);

        Map<String, TemperatureReading> expectedTemperatures = new HashMap<>();
        expectedTemperatures.put("device1", TemperatureNotAvailable.INSTANCE);
        expectedTemperatures.put("device2", new Temperature(2.0));

        assertEquals(expectedTemperatures, response.temperatures);
    }

    @Test
    public void testReturnDeviceNotAvailableIfDeviceStopsBeforeAnswering() {
        TestProbe<RespondAllTemperatures> requester =
            testKit.createTestProbe(RespondAllTemperatures.class);
        TestProbe<Device.Command> device1 = testKit.createTestProbe(Device.Command.class);
        TestProbe<Device.Command> device2 = testKit.createTestProbe(Device.Command.class);

        Map<String, ActorRef<Device.Command>> deviceIdToActor = new HashMap<>();
        deviceIdToActor.put("device1", device1.getRef());
        deviceIdToActor.put("device2", device2.getRef());

        ActorRef<DeviceGroupQuery.Command> queryActor =
            testKit.spawn(
                DeviceGroupQuery.create(
                    deviceIdToActor, 1L, requester.getRef(), Duration.ofSeconds(3)));

        assertEquals(0L, device1.expectMessageClass(Device.ReadTemperature.class).requestId);
        assertEquals(0L, device2.expectMessageClass(Device.ReadTemperature.class).requestId);

        queryActor.tell(
            new DeviceGroupQuery.WrappedRespondTemperature(
                new Device.RespondTemperature(0L, "device1", Optional.of(1.0))));

        device2.stop();

        RespondAllTemperatures response = requester.receiveMessage();
        assertEquals(1L, response.requestId);

        Map<String, TemperatureReading> expectedTemperatures = new HashMap<>();
        expectedTemperatures.put("device1", new Temperature(1.0));
        expectedTemperatures.put("device2", DeviceNotAvailable.INSTANCE);

        assertEquals(expectedTemperatures, response.temperatures);
    }

    @Test
    public void testReturnTemperatureReadingEvenIfDeviceStopsAfterAnswering() {
        TestProbe<RespondAllTemperatures> requester =
            testKit.createTestProbe(RespondAllTemperatures.class);
        TestProbe<Device.Command> device1 = testKit.createTestProbe(Device.Command.class);
        TestProbe<Device.Command> device2 = testKit.createTestProbe(Device.Command.class);

        Map<String, ActorRef<Device.Command>> deviceIdToActor = new HashMap<>();
        deviceIdToActor.put("device1", device1.getRef());
        deviceIdToActor.put("device2", device2.getRef());

        ActorRef<DeviceGroupQuery.Command> queryActor =
            testKit.spawn(
                DeviceGroupQuery.create(
                    deviceIdToActor, 1L, requester.getRef(), Duration.ofSeconds(3)));

        assertEquals(0L, device1.expectMessageClass(Device.ReadTemperature.class).requestId);
        assertEquals(0L, device2.expectMessageClass(Device.ReadTemperature.class).requestId);

        queryActor.tell(
            new DeviceGroupQuery.WrappedRespondTemperature(
                new Device.RespondTemperature(0L, "device1", Optional.of(1.0))));

        queryActor.tell(
            new DeviceGroupQuery.WrappedRespondTemperature(
                new Device.RespondTemperature(0L, "device2", Optional.of(2.0))));

        device2.stop();

        RespondAllTemperatures response = requester.receiveMessage();
        assertEquals(1L, response.requestId);

        Map<String, TemperatureReading> expectedTemperatures = new HashMap<>();
        expectedTemperatures.put("device1", new Temperature(1.0));
        expectedTemperatures.put("device2", new Temperature(2.0));

        assertEquals(expectedTemperatures, response.temperatures);
    }

    @Test
    public void testReturnDeviceTimedOutIfDeviceDoesNotAnswerInTime() {
        TestProbe<RespondAllTemperatures> requester =
            testKit.createTestProbe(RespondAllTemperatures.class);
        TestProbe<Device.Command> device1 = testKit.createTestProbe(Device.Command.class);
        TestProbe<Device.Command> device2 = testKit.createTestProbe(Device.Command.class);

        Map<String, ActorRef<Device.Command>> deviceIdToActor = new HashMap<>();
        deviceIdToActor.put("device1", device1.getRef());
        deviceIdToActor.put("device2", device2.getRef());

        ActorRef<DeviceGroupQuery.Command> queryActor =
            testKit.spawn(
                DeviceGroupQuery.create(
                    deviceIdToActor, 1L, requester.getRef(), Duration.ofMillis(200)));

        assertEquals(0L, device1.expectMessageClass(Device.ReadTemperature.class).requestId);
        assertEquals(0L, device2.expectMessageClass(Device.ReadTemperature.class).requestId);

        queryActor.tell(
            new DeviceGroupQuery.WrappedRespondTemperature(
                new Device.RespondTemperature(0L, "device1", Optional.of(1.0))));

        // no reply from device2

        RespondAllTemperatures response = requester.receiveMessage();
        assertEquals(1L, response.requestId);

        Map<String, TemperatureReading> expectedTemperatures = new HashMap<>();
        expectedTemperatures.put("device1", new Temperature(1.0));
        expectedTemperatures.put("device2", DeviceTimedOut.INSTANCE);

        assertEquals(expectedTemperatures, response.temperatures);
    }

    @Test
    public void testCollectTemperaturesFromAllActiveDevices() {
        TestProbe<DeviceRegistered> registeredProbe = testKit.createTestProbe(DeviceRegistered.class);
        ActorRef<DeviceGroup.Command> groupActor = testKit.spawn(DeviceGroup.create("group"));

        groupActor.tell(new RequestTrackDevice("group", "device1", registeredProbe.getRef()));
        ActorRef<Device.Command> deviceActor1 = registeredProbe.receiveMessage().device;

        groupActor.tell(new RequestTrackDevice("group", "device2", registeredProbe.getRef()));
        ActorRef<Device.Command> deviceActor2 = registeredProbe.receiveMessage().device;

        groupActor.tell(new RequestTrackDevice("group", "device3", registeredProbe.getRef()));
        ActorRef<Device.Command> deviceActor3 = registeredProbe.receiveMessage().device;

        // Check that the device actors are working
        TestProbe<Device.TemperatureRecorded> recordProbe =
            testKit.createTestProbe(Device.TemperatureRecorded.class);
        deviceActor1.tell(new Device.RecordTemperature(0L, 1.0, recordProbe.getRef()));
        assertEquals(0L, recordProbe.receiveMessage().requestId);
        deviceActor2.tell(new Device.RecordTemperature(1L, 2.0, recordProbe.getRef()));
        assertEquals(1L, recordProbe.receiveMessage().requestId);
        // No temperature for device 3

        TestProbe<RespondAllTemperatures> allTempProbe =
            testKit.createTestProbe(RespondAllTemperatures.class);
        groupActor.tell(new RequestAllTemperatures(0L, "group", allTempProbe.getRef()));
        RespondAllTemperatures response = allTempProbe.receiveMessage();
        assertEquals(0L, response.requestId);

        Map<String, TemperatureReading> expectedTemperatures = new HashMap<>();
        expectedTemperatures.put("device1", new Temperature(1.0));
        expectedTemperatures.put("device2", new Temperature(2.0));
        expectedTemperatures.put("device3", TemperatureNotAvailable.INSTANCE);

        assertEquals(expectedTemperatures, response.temperatures);
    }

}