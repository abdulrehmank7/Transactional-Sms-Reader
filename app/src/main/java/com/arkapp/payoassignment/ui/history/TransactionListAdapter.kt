package com.arkapp.payoassignment.ui.history

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.arkapp.payoassignment.R
import com.arkapp.payoassignment.data.models.Transaction
import com.arkapp.payoassignment.databinding.RvTransactionBinding
import com.arkapp.payoassignment.ui.home.convertTransaction
import com.arkapp.payoassignment.utils.DEBIT
import com.arkapp.payoassignment.utils.getFormattedDate
import com.arkapp.payoassignment.utils.getFormattedTime

/**
 * Created by Abdul Rehman on 28-02-2020.
 * Contact email - abdulrehman0796@gmail.com
 */

class TransactionListAdapter(private val allTransactions: List<Transaction>,
                             private val context: Context,
                             private val lifecycleScope: LifecycleCoroutineScope) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TransactionListViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.rv_transaction,
                parent,
                false
            )
        )
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as TransactionListViewHolder).viewBinding
        val transaction = convertTransaction(allTransactions[position])

        binding.tvType.text = transaction.type
        binding.tvDate.text = transaction.date!!.getFormattedDate()
        binding.tvTime.text = transaction.date!!.getFormattedTime()

        val sign =
            if (transaction.type == DEBIT) {
                setDebitUi(binding)
                "-"
            } else {
                setCreditUi(binding)
                "+"
            }

        binding.tvAmount.text = "$sign ${context.getString(R.string.Rs)}${transaction.amount.toString()}"

        binding.btTag.setOnClickListener {
            DialogAddTag(context, lifecycleScope, transaction.id!!).show()
        }
    }

    private fun setDebitUi(binding: RvTransactionBinding) {
        binding.icArrow.setCardBackgroundColor(ContextCompat.getColor(context, R.color.red))
        binding.tvType.setTextColor(ContextCompat.getColor(context, R.color.red))
        binding.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.red))
        binding.ivIcon.rotation = -135f
    }

    private fun setCreditUi(binding: RvTransactionBinding) {
        binding.icArrow.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
        binding.tvType.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
        binding.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
        binding.ivIcon.rotation = 45f
    }


    override fun getItemCount() = allTransactions.size

    override fun getItemId(position: Int): Long {
        return allTransactions[position].hashCode().toLong()
    }

}