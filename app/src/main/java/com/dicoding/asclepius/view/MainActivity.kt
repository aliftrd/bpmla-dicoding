package com.dicoding.asclepius.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.source.local.entity.History
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.dicoding.asclepius.utils.Number
import com.dicoding.asclepius.view.adapter.NewsAdapter
import com.dicoding.asclepius.view.viewmodel.MainViewModel
import com.dicoding.asclepius.view.viewmodel.ViewModelFactory
import com.yalantis.ucrop.UCrop
import org.tensorflow.lite.task.vision.classifier.Classifications

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var imageClassifierHelper: ImageClassifierHelper

    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val mainViewModel = obtainViewModel(this)

        imageClassifierHelper = ImageClassifierHelper(
            context = this,
            classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                    runOnUiThread {
                        binding.progressIndicator.visibility = View.GONE
                        results?.let {
                            val category = it[0].categories[0].label
                            val confidence = it[0].categories[0].score

                            currentImageUri?.let { uri ->
                                this@MainActivity.contentResolver.openInputStream(
                                    uri
                                )?.use { it.buffered().readBytes() }
                            }
                                ?.let { image ->
                                    val history = History(
                                        image = image,
                                        category = category,
                                        score = confidence
                                    )

                                    mainViewModel.createHistory(history)
                                }
                            moveToResult(
                                "Category: $category, Confidence: ${
                                    Number.decimalToPercentage(
                                        confidence
                                    )
                                }"
                            )
                        }
                    }
                }

                override fun onError(message: String) {
                    runOnUiThread {
                        binding.progressIndicator.visibility = View.GONE
                        showToast(message)
                    }
                }
            }
        )

        mainViewModel.news.observe(this) {
            binding.newsList.layoutManager = LinearLayoutManager(this)
            binding.newsList.adapter = NewsAdapter().apply { submitList(it) }
        }

        mainViewModel.isLoading.observe(this) {
            binding.progressIndicator.visibility = if (it) View.VISIBLE else View.GONE
        }

        mainViewModel.isError.observe(this) {
            if (it) showToast(getString(R.string.failed_to_load_news))
        }

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.analyzeButton.setOnClickListener { analyzeImage() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_meu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.history_action -> Intent(
                this,
                HistoryActivity::class.java
            ).also { startActivity(it) }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            UCrop.of(uri, Uri.fromFile(cacheDir.resolve("temp_image.jpg")))
                .withAspectRatio(1f, 1f)
                .withMaxResultSize(224, 224)
                .start(this)
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri: Uri? = UCrop.getOutput(data!!)
            currentImageUri = resultUri
            showImage()
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError: Throwable? = UCrop.getError(data!!)
            Log.e("Crop Error", "onActivityResult: ", cropError)
        }
    }


    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun analyzeImage() {
        if (currentImageUri != null) {
            binding.progressIndicator.visibility = View.VISIBLE
            currentImageUri?.let { imageClassifierHelper.classifyStaticImage(it) }
        } else {
            showToast(getString(R.string.please_select_an_image_first))
        }
    }

    private fun moveToResult(prediction: String) {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra(ResultActivity.EXTRA_IMAGE, currentImageUri.toString())
        intent.putExtra(ResultActivity.EXTRA_PREDICTION, prediction)
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun obtainViewModel(activity: AppCompatActivity): MainViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[MainViewModel::class.java]
    }
}