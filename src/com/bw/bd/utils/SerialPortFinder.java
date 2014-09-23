package com.bw.bd.utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Iterator;
import java.util.Vector;

public class SerialPortFinder {
	private static final String TAG = SerialPortFinder.class.getSimpleName();
	private static final boolean DBG = true;

	private Vector<Drvier> mDrivers = null;

	public class Drvier {

		public Drvier(String name, String root) {
			mDriverName = name;
			mDeviceRoot = root;
		}

		private String mDriverName;
		private String mDeviceRoot;
		Vector<File> mDevices = null;

		public Vector<File> getDevices() {
			if (mDevices == null) {
				mDevices = new Vector<File>();
				File dev = new File("/dev");
				File[] files = dev.listFiles();
				for (int i = 0; i < files.length; i++) {
					if (files[i].getAbsolutePath().startsWith(mDeviceRoot)) {
						mDevices.add(files[i]);
						// log.e(TAG, "find new device:::" + files[i]);
					}
				}
			}

			return mDevices;
		}

		public String getName() {
			return mDriverName;
		}
	}

	public Vector<Drvier> getDrivers() throws IOException {
		mDrivers = new Vector<SerialPortFinder.Drvier>();
		LineNumberReader lnr = new LineNumberReader(new FileReader("/proc/tty/drivers"));
		String line;
		while ((line = lnr.readLine()) != null) {
			// Since driver name may contain spaces, we do not extract
			// driver name with split()
			String driverName = line.substring(0, 0X15).trim();
			String[] pecies = line.split(" +");
			if ((pecies.length >= 5) && (pecies[pecies.length - 1].equals("serial"))) {
				// log.e(TAG, "Find new driver:" + driverName + " on " +
				// pecies[pecies.length - 4]);
				mDrivers.add(new Drvier(driverName, pecies[pecies.length - 4]));
			}
		}
		lnr.close();
		return mDrivers;
	}

	public String[] getAllDevices() {
		Vector<String> devices = new Vector<String>();
		Iterator<Drvier> itr;
		try {
			itr = getDrivers().iterator();
			while (itr.hasNext()) {
				Drvier driver = itr.next();
				Iterator<File> itrDevice = driver.getDevices().iterator();
				while (itrDevice.hasNext()) {
					String deviceName = itrDevice.next().getName();
					String value = String.format("%s(%s)", deviceName, driver.getName());
					devices.add(value);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return devices.toArray(new String[devices.size()]);
	}

	public String[] getAllDevicesPath() {
		Vector<String> devices = new Vector<String>();
		Iterator<Drvier> itr;
		try {
			itr = getDrivers().iterator();
			while (itr.hasNext()) {
				Drvier driver = itr.next();
				Iterator<File> itrDevice = driver.getDevices().iterator();
				while (itrDevice.hasNext()) {
					String devicePath = itrDevice.next().getAbsolutePath();
					devices.add(devicePath);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return devices.toArray(new String[devices.size()]);
	}
}
