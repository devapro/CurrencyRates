package pro.devapp.currencyrates.ui.rates

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pro.devapp.currencyrates.databinding.FragmentRatesBinding
import pro.devapp.currencyrates.ui.viewBinding
import pro.devapp.currencyrates.usecases.CreateCurrencyByCodeUseCase
import pro.devapp.currencyrates.usecases.GetRatesListUseCase
import pro.devapp.storage.Storage

class RatesFragment : Fragment() {

    companion object {
        fun newInstance() = RatesFragment()
    }

    private val viewModel by viewModels<RatesViewModel>() {
        object : ViewModelProvider.NewInstanceFactory() {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return RatesViewModel(
                    requireActivity().application,
                    GetRatesListUseCase(Storage.getCurrencyRatesRepository(requireActivity().applicationContext)),
                    CreateCurrencyByCodeUseCase(Storage.getCurrencyDetailsRepository(requireActivity().applicationContext))
                ) as T
            }
        }
    }

    private val screenBinding by viewBinding(FragmentRatesBinding::inflate)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        screenBinding.currencyList.selectItemListener = {
            viewModel.setSelectedCurrency(it)
        }

        screenBinding.currencyList.setTextListener {
            viewModel.setValue(it)
        }

        viewModel.currencyList.observe(viewLifecycleOwner) {
            if (screenBinding.progress.visibility != View.GONE) {
                screenBinding.progress.visibility = View.GONE
                screenBinding.currencyList.visibility = View.VISIBLE
            }
            screenBinding.currencyList.submitList(it)
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        return screenBinding.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.startRefreshList()
    }

    override fun onStop() {
        super.onStop()
        viewModel.stopRefreshList()
    }
}