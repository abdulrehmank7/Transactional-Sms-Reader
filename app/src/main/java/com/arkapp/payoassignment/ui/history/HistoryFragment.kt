package com.arkapp.payoassignment.ui.history

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.arkapp.payoassignment.R
import com.arkapp.payoassignment.utils.TRANSACTIONAL_LIST
import com.arkapp.payoassignment.utils.initVerticalAdapter
import kotlinx.android.synthetic.main.fragment_history.*


class HistoryFragment : Fragment(R.layout.fragment_history) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = TransactionListAdapter(TRANSACTIONAL_LIST, requireContext(), lifecycleScope)
        rvTransaction.initVerticalAdapter(adapter, true)
    }

}