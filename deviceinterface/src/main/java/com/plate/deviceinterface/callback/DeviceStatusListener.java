package com.plate.deviceinterface.callback;

import com.plate.deviceinterface.model.DeviceHardwareInfo;

public interface DeviceStatusListener {
    void onAttachDevice(DeviceHardwareInfo deviceHardwareInfo);
    void onDetachDevice(DeviceHardwareInfo deviceHardwareInfo);
}
