using System;

namespace TaskTracker.Services;

public interface INavigationService
{
    public object CurrentContent { get; }

    public event EventHandler<NavigationEventArgs> Navigated;

    public void NavigateTo(object content);
}
