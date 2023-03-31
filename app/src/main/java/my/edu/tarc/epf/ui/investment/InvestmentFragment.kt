package my.edu.tarc.epf.ui.investment

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import my.edu.tarc.epf.databinding.FragmentInvestmentBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.*

class InvestmentFragment : Fragment() {

    private var _binding: FragmentInvestmentBinding? = null
    var calendar = getInstance()
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val investmentViewModel =
            ViewModelProvider(this).get(InvestmentViewModel::class.java)

        _binding = FragmentInvestmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonDOB.setOnClickListener {
            val dateDialogFragment = DateDialogFragment{
                    year, month, day ->  onDateSelected(year, month, day)
            }
            dateDialogFragment.show(parentFragmentManager,
                "DateDialog")
        }

        binding.buttonCalculate.setOnClickListener {  }
        binding.buttonReset.setOnClickListener {  }
    }


    private fun onDateSelected(year: Int, month: Int, day: Int) {
        binding.buttonDOB.text =
            String.format("%02d/%02d/%d", day, month+1, year)
        val dob = getInstance()
        with(dob){
            set(YEAR, year)
            set(MONTH, month)
            set(DAY_OF_MONTH, day)
        }
        val today = getInstance()
        val age = daysBetween(dob, today).div(365)
        binding.textViewAge.text = age.toString()
    }

    private fun daysBetween(startDate: Calendar, endDate: Calendar?): Long {
        val date = startDate.clone() as Calendar
        var daysBetween: Long = 0
        while (date.before(endDate)) {
            date.add(DAY_OF_MONTH, 1)
            daysBetween++
        }
        return daysBetween
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class DateDialogFragment(
        val dateSetListener:(year: Int, month: Int, day: Int) -> Unit): DialogFragment(),
        DatePickerDialog.OnDateSetListener{
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val c = Calendar.getInstance()
            val year = c.get(YEAR)
            val month = c.get(MONTH)
            val day = c.get(DAY_OF_MONTH)
            return DatePickerDialog(requireContext(), this,
                year, month, day)
        }

        override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
            dateSetListener(year, month, dayOfMonth)
        }

    }
}