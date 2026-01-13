using TaskTracker.Models;

namespace TaskTracker.Services;

public class IdentityService : IIdentityService
{
    private readonly IRequestProvider _requestProvider;
    private readonly ISettingsService _settingsService;

    public IdentityService(IRequestProvider requestProvider, ISettingsService settingsService)
    {
        _requestProvider = requestProvider;
        _settingsService = settingsService;
    }

    public async System.Threading.Tasks.Task LogIn(User user)
    {
        var result = await _requestProvider
            .PostAsync<User, string>("/login", user, _settingsService.Token)
            .ConfigureAwait(false);

        if (!string.IsNullOrEmpty(result))
        {
            _settingsService.Token = result;
            _settingsService.CurrentUser = user;
        }
    }

    public async System.Threading.Tasks.Task Register(User user)
    {
        var result = await _requestProvider
            .PostAsync<User, RegisterResponse?>("/register", user, _settingsService.Token)
            .ConfigureAwait(false);

        if (result is not null && !string.IsNullOrEmpty(result.Value.Token))
        {
            _settingsService.Token = result.Value.Token;
            _settingsService.CurrentUser = result.Value.User;
        }
    }
    
    struct RegisterResponse
    {
        public User User { get; set; }

        public string Token { get; set; }
    }
}
