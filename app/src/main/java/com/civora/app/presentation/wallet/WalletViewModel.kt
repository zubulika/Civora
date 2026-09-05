package com.civora.app.presentation.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.civora.app.core.model.DigitalDocument
import com.civora.app.domain.usecase.GetUserDocumentsUseCase
import com.civora.app.data.mock.CivoraMockDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn

class WalletViewModel(
    getUserDocumentsUseCase: GetUserDocumentsUseCase
) : ViewModel() {

    val documents: StateFlow<List<DigitalDocument>> = getUserDocumentsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = CivoraMockDataSource.documents
        )

    private val _selectedDocumentId = MutableStateFlow<String?>(null)
    val selectedDocumentId: StateFlow<String?> = _selectedDocumentId.asStateFlow()

    fun selectDocument(id: String?) {
        _selectedDocumentId.value = if (_selectedDocumentId.value == id) null else id
    }

    companion object {
        fun provideFactory(useCase: GetUserDocumentsUseCase): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return WalletViewModel(useCase) as T
                }
            }
    }
}
