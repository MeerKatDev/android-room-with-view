package com.example.roomtestwithblocks

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.roomtestwithblocks.WordsApplication.Companion.database
import com.example.roomtestwithblocks.adapters.WordListAdapter
import com.example.roomtestwithblocks.data.Word
import com.example.roomtestwithblocks.data.WordRepo
import com.example.roomtestwithblocks.ui.WordViewModel
import com.example.roomtestwithblocks.ui.WordViewModelFactory
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : ComponentActivity() {

    private val wordViewModel: WordViewModel by viewModels {

        val repository by lazy { WordRepo(database.wordDao()) }
        WordViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = WordListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Add an observer on the LiveData returned by getAlphabetizedWords.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        CoroutineScope(Dispatchers.IO).launch {
            wordViewModel.allWords.collect { words ->
            // Update the cached copy of the words in the adapter.
                withContext(Dispatchers.Main) {
                    adapter.submitList(words)
                }
            }
        }


        val fab = findViewById<ExtendedFloatingActionButton>(R.id.fab)
        fab.setTextColor(ContextCompat.getColor(this, R.color.black))
        fab.setBackgroundColor(ContextCompat.getColor(this, R.color.teal_200))
        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, NewWordActivity::class.java)
            activityLauncher.launch(intent)
        }
    }

    private val activityLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.getStringExtra(NewWordActivity.EXTRA_REPLY)?.let { reply ->
                val word = Word(reply)
                wordViewModel.insert(word)
            }
        } else {
            Toast.makeText(
                applicationContext,
                R.string.empty_not_saved,
                Toast.LENGTH_LONG
            ).show()
        }
    }

}