import serial
import numpy as np
import pandas as pd
from time import sleep, time, perf_counter
from datetime import datetime


'''smooth_sample
Code from before I started working on the project.
Turns the data into more of a wave form.
'''
def smooth_sample(sample, smoothing=10):
    frame = pd.DataFrame(sample).rolling(smoothing).mean()
    frame = frame.shift(periods=-smoothing // 2)
    norm = np.subtract(sample, frame.values.reshape(len(sample), ))
    return norm


'''collect_sample
Function to record data from arduino connected to computer.

You need to identify and set which port you Arduino is connected to. Something like "/dev/tty.usbmodem14201" or "COM3"
If you need to record for longer, then adjust the 'sample_len', 'duration', or 'max_num_zeroes' properties.
'max_num_zeroes' will stop recording after a certain number of zeroes are read.
'''
def collect_sample(sample_len=3500, duration=7, max_num_zeroes=200):
    arduino = serial.Serial('/dev/tty.usbmodem14201', 9600, timeout=.1)
    arduino.flushInput()
    cnt = 0
    prevVal = 0
    sample = np.zeros(sample_len)
    print("Starting Collection...")
    init_time = datetime.now()

    positives = 0
    zero_counter = 0
    try:
        while (abs(init_time - datetime.now()).total_seconds()) <= duration:  # switch to true to actually run it
            raw_data = arduino.readline()  # the last bit gets rid of the new-line chars
            data = raw_data[:-2]
            if data:
                if zero_counter >= max_num_zeroes:
                    break

                try:
                    value = float(data)
                    if value > 0:
                        positives += 1
                    elif value == 0 and positives > 2:
                        zero_counter += 1

                    if value != 0:
                        zero_counter = 0

                    prevVal = value

                except ValueError:
                    print(f"Error Casting {data}")
                    value = prevVal

                sample[cnt] = value
                cnt += 1
        # if cnt > LEN_TEST - 1:
        return (sample[:cnt], cnt)

    except KeyboardInterrupt:
        pass
    finally:
        arduino.close()
        print(f"Collection Complete: {cnt} data points read")


raw_data = collect_sample()
smooth_data = smooth_sample(raw_data)

print("You now have the vibration data. You can print it, save it to a file, create graphs, or copy code from the Vibe Server to process it")