package be.nextapps.jonas.bap_todo_android

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Context
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.getSystemService
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.*

class AlarmFragment : Fragment(){

    lateinit var alarmButton: Button;
    lateinit var enabledCheckbox: CheckBox;
    lateinit var locationButton: Button;

    private var networkFragment: NetworkFragment? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient;

    @SuppressLint("MissingPermission")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.alarm_fragment, container, false);

        val activity: FragmentActivity? = getActivity();
        val sharedPref = activity?.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        alarmButton = view.findViewById(R.id.alarm_time)
        enabledCheckbox = view.findViewById(R.id.alarm_on)
        locationButton = view.findViewById(R.id.location_button)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)

        val currentAlarm: String = sharedPref!!.getString(getString(R.string.alarm), "none")
        alarmButton.setText(currentAlarm);
        val enabled: Boolean = sharedPref!!.getString(getString(R.string.alarm_enabled), "false").toBoolean()
        enabledCheckbox.isChecked = enabled;

        alarmButton.setOnClickListener(View.OnClickListener {
            if(it is Button){
                val c = Calendar.getInstance()
                val hour = c.get(Calendar.HOUR_OF_DAY)
                val minute = c.get(Calendar.MINUTE)

                val tpd = TimePickerDialog(
                    activity, TimePickerDialog.OnTimeSetListener { view, hour, minute ->
                        val timeString = "$hour:$minute"
                        alarmButton.setText(timeString);

                        with(sharedPref!!.edit()){
                            putString(getString(R.string.alarm), timeString);
                            apply();
                        }
                    }, hour, minute, true)
                tpd.show()
            }
        })

        enabledCheckbox.setOnClickListener(View.OnClickListener {
            if(it is CheckBox){
                val checked = it.isChecked;
                with(sharedPref!!.edit()){
                    putString(getString(R.string.alarm_enabled), checked.toString());
                    apply();
                }
            }
        })

        locationButton.setOnClickListener(View.OnClickListener {
            if(it is Button){
                fusedLocationClient.lastLocation
                    .addOnSuccessListener {
                        location: Location? ->
                        var url = "https://api.timezonedb.com/v2.1/get-time-zone?key=BNC3MFRJAMK4&format=json&by=position&lat=${location?.latitude}&lng=${location?.longitude}"

                        networkFragment = NetworkFragment.getInstance(activity.supportFragmentManager, url)
                        startDownloading(url)
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "getting location failed", Toast.LENGTH_LONG).show()
                    }
            }
        })



        return view;
    }

    private fun startDownloading(url: String){
        val sharedPref = activity?.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        with(sharedPref!!.edit()){
            putString(getString(R.string.url), url);
            apply();
        }
        networkFragment?.apply{
            startDownload();
        }
    }

}