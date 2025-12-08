using Avalonia.Controls;
using System;

namespace TaskTracker.Services;

internal class NavigationService : INavigationService
{
    private object _root;

    public object CurrentContent => throw new NotImplementedException();

    public event EventHandler<NavigationEventArgs> Navigated;

    public void NavigateTo(object content)
    {
        var frame = _root as TransitioningContentControl;
        if (frame is null)
            return;

        frame.Content = content;

        Navigated?.Invoke(this, new NavigationEventArgs(content));
    }

    public void SetRoot(object root)
    {
        _root = root;
    }
}
