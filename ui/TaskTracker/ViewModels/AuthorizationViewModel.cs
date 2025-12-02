using CommunityToolkit.Mvvm.ComponentModel;
using CommunityToolkit.Mvvm.Input;
using TaskTracker.Models;
using TaskTracker.Repositories;

namespace TaskTracker.ViewModels;

public partial class AuthorizationViewModel : ViewModelBase
{
    private UserRepository _userRepository;

    [ObservableProperty]
    private string _login = string.Empty;

    [ObservableProperty]
    private string _password = string.Empty;

    [ObservableProperty]
    private string _passwordConfirmation = string.Empty;

    public bool CanRegister =>
        Password.Equals(PasswordConfirmation, System.StringComparison.Ordinal);

    [ObservableProperty]
    private User? _currentUser;

    [ObservableProperty]
    private bool _needRegistration = false;

    public AuthorizationViewModel(UserRepository userRepository)
    {
        _userRepository = userRepository;
    }

    [RelayCommand(AllowConcurrentExecutions = true)]
    private async System.Threading.Tasks.Task LogIn()
    {
        await System.Threading.Tasks.Task.CompletedTask.ConfigureAwait(false);

        CurrentUser = new User();
    }

    [RelayCommand(AllowConcurrentExecutions = true)]
    private async System.Threading.Tasks.Task Registration()
    {
        await System.Threading.Tasks.Task.CompletedTask.ConfigureAwait(false);

        CurrentUser = new User();
    }
}
