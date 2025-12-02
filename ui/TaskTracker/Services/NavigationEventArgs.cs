using System.Diagnostics.CodeAnalysis;

namespace TaskTracker.Services;

[SuppressMessage("Style", "CA1711", Justification = "Данный класс представляет аргументы эвента, поэтому в названии EventArgs")]
public class NavigationEventArgs
{
    public object Content { get; }

    public NavigationEventArgs(object content)
    {
        Content = content;
    }
}
