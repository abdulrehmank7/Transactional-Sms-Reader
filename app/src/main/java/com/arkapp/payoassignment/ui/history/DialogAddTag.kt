package com.arkapp.payoassignment.ui.history

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.lifecycle.LifecycleCoroutineScope
import com.arkapp.payoassignment.databinding.DialogAddTagBinding
import com.arkapp.payoassignment.utils.setFullWidth
import com.arkapp.payoassignment.utils.setTransparentEdges
import com.arkapp.payoassignment.utils.value

/**
 * Created by Abdul Rehman.
 * Contact email - abdulrehman0796@gmail.com
 */
class DialogAddTag(context: Context, private val lifecycleScope: LifecycleCoroutineScope, private val id: Int) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        val binding = DialogAddTagBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
        window?.setFullWidth()
        window?.setTransparentEdges()

        binding.doneBtn.setOnClickListener { v ->
            if (binding.etTag.text.toString().isEmpty()) {
                binding.tag.error = "Please enter tag!"
                return@setOnClickListener
            }
            context.updateTag(lifecycleScope, binding.etTag.value(), id)
            dismiss()
        }
    }
}