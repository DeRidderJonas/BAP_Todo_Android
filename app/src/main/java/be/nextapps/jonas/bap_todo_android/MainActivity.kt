package be.nextapps.jonas.bap_todo_android

import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject

class MainActivity : AppCompatActivity(), DownloadCallback<String> {

    private var networkFragment: NetworkFragment? = null;
    private var downloading = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragmentAdapter = MyPageAdapter(supportFragmentManager);
        viewpager_main.adapter = fragmentAdapter;

        tabs_main.setupWithViewPager(viewpager_main);

        var url = "https://api.timezonedb.com/v2.1/get-time-zone?key=BNC3MFRJAMK4&format=json&fields=abbreviation,formatted&by=position&lat=40.7127837&lng=-74.0059413"

        networkFragment = NetworkFragment.getInstance(supportFragmentManager, url)

    }

    private fun startDownloading(){
        if(!downloading){
            networkFragment?.apply{
                startDownload();
                downloading = true;
            }
        }
    }

    override fun updateFromDownload(result: String?) {
        val jsonObject = JSONObject(result)
        Toast.makeText(baseContext, "Timezone: ${jsonObject.get("abbreviation")}, time: ${jsonObject.get("formatted")}", Toast.LENGTH_LONG).show()
    }

    override fun getActiveNetworkInfo(): NetworkInfo {
        val connectionManager = ContextCompat.getSystemService<ConnectivityManager>(
            applicationContext,
            ConnectivityManager::class.java
        ) as ConnectivityManager
        return connectionManager.activeNetworkInfo
    }

    override fun onProgressUpdate(progressCode: Int, percentComplete: Int) {
        when(progressCode){
            ERROR -> {
                println("Error with HTTP request")
            }
            CONNECT_SUCCESS -> {
                println("Connect success")
            }
            GET_INPUT_STREAM_SUCCESS -> {
                println("Get input stream success")
            }
            PROCESS_INPUT_STREAM_IN_PROGRESS -> {
                println("Process input stream in progress")
            }
            PROCESS_INPUT_STREAM_SUCCESS -> {
                println("Process input stream success")
            }
        }
    }

    override fun finishDownloading() {
        downloading = false;
        networkFragment?.cancelDownload()
    }
}
