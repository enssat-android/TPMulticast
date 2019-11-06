package fr.enssat.tpmulticast

import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import fr.enssat.tpmulticast.databinding.ActivityMainBinding


class MessageActivity : AppCompatActivity() {
    private val TAG = this.javaClass.simpleName

    //attention le nom de votre data binding est une convention
    //ici le layout s'appelle activity_main.xml donc
    // le data binding est ActivityMainBinding
    // ne pas publier dans le build.gradle
    // dataBinding {
    //        enabled = true
    //    }
    // https://medium.com/@temidjoy/android-jetpack-empower-your-ui-with-android-data-binding-94a657cb6be1
    //

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val model = ViewModelProviders.of(this, MulticastViewModelFactory(this)).get(MultiCastViewModel::class.java)


        binding.createMessageButton.setOnClickListener {
            model.send(binding.messageEditText.text.toString())
        }

        val adapter = MessageAdapter {}
        binding.messageList.adapter = adapter

        model.messages.observe(this, Observer { list ->
            adapter.list = list
        })

    }

}
