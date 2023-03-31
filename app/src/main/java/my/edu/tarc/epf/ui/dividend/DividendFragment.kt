package my.edu.tarc.epf.ui.dividend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import my.edu.tarc.epf.databinding.FragmentDividendBinding

class DividendFragment : Fragment() {

    private var _binding: FragmentDividendBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dividendViewModel =
            ViewModelProvider(this).get(DividendViewModel::class.java)

        _binding = FragmentDividendBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}