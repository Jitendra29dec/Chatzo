package com.example.chatzo.Activity

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.chatzo.R
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.shockwave.pdfium.PdfDocument.Bookmark
import com.shockwave.pdfium.PdfDocument.Meta
import java.io.File

class ActViewPdf constructor() : AppCompatActivity(), OnPageChangeListener, OnLoadCompleteListener {
    var pdfView: PDFView? = null
    var pageNumber: Int = 0
    var pdfFileName: String? = null
    var tv_header: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_view_pdf)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.action_color))
        }
        pdfView = findViewById<View>(R.id.pdfView) as PDFView?
        tv_header = findViewById<View>(R.id.tv_header) as TextView?
        displayFromAsset(getIntent().getStringExtra("filename"))
        tv_header!!.setText(getIntent().getStringExtra("name"))
        // Toast.makeText(ActViewPdf.class,"Name "+getIntent().getStringExtra("filename"),Toast.LENGTH_SHORT).show();
    }

    private fun displayFromAsset(assetFileName: String) {
        pdfFileName = assetFileName
        pdfView!!.fromFile(File(assetFileName))
                .defaultPage(pageNumber)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(DefaultScrollHandle(this))
                .load()
        //        pdfView.fromAsset(SAMPLE_FILE)
//                .defaultPage(pageNumber)
//                .enableSwipe(true)
//
//                .swipeHorizontal(false)
//                .onPageChange(this)
//                .enableAnnotationRendering(true)
//                .onLoad(this)
//                .scrollHandle(new DefaultScrollHandle(this))
//                .load();
    }

    public override fun onPageChanged(page: Int, pageCount: Int) {
        pageNumber = page
        setTitle(String.format("%s %s / %s", pdfFileName, page + 1, pageCount))
    }

    public override fun loadComplete(nbPages: Int) {
        val meta: Meta = pdfView!!.getDocumentMeta()
        printBookmarksTree(pdfView!!.getTableOfContents(), "-")
    }

    fun printBookmarksTree(tree: List<Bookmark>, sep: String) {
        for (b: Bookmark in tree) {
            Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()))
            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-")
            }
        }
    }

    companion object {
        private val TAG: String = ActViewPdf::class.java.getSimpleName()
        val SAMPLE_FILE: String = "android_tutorial.pdf"
    }
}