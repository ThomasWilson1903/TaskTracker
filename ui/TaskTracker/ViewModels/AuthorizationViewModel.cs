using CommunityToolkit.Mvvm.ComponentModel;
using CommunityToolkit.Mvvm.Input;
using TaskTracker.Models;
using TaskTracker.Repositories;
using TaskTracker.Services;

namespace TaskTracker.ViewModels;

public partial class AuthorizationViewModel : ViewModelBase
{
    private IRouter _router;
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

    public AuthorizationViewModel(IRouter router,UserRepository userRepository)
    {
        _router = router;
        _userRepository = userRepository;
    }

    [RelayCommand(AllowConcurrentExecutions = true)]
    private async System.Threading.Tasks.Task LogIn()
    {
        await System.Threading.Tasks.Task.CompletedTask.ConfigureAwait(false);

        CurrentUser = new User();

        await _userRepository.LoginAsync(CurrentUser).ConfigureAwait(false);
        NavigateToTasks();
    }

    [RelayCommand(AllowConcurrentExecutions = true)]
    private async System.Threading.Tasks.Task Registration()
    {
        await System.Threading.Tasks.Task.CompletedTask.ConfigureAwait(false);

        CurrentUser = new User();
        await _userRepository.RegisterAsync(CurrentUser).ConfigureAwait(false);
        NavigateToTasks();
    }

    private void NavigateToTasks()
    {
        _router.NavigateTo("/tasks");
    }
}
