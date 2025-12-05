import { Input } from "@/components/ui/input";
import { useState, useEffect } from "react";
import { Search } from "lucide-react";

interface UserSearchProps {
    value: string;
    onChange: (value: string) => void;
    onSearch: () => void;
    placeholder?: string;
}

export function UserSearch({ value, onChange, onSearch, placeholder = "Search users..." }: UserSearchProps) {
    const [localValue, setLocalValue] = useState(value);

    // Sync local value with prop value if it changes externally
    useEffect(() => {
        setLocalValue(value);
    }, [value]);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const newValue = e.target.value;
        setLocalValue(newValue);
        onChange(newValue);
    };

    const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
        if (e.key === 'Enter') {
            onSearch();
        }
    };

    return (
        <div className="relative max-w-sm">
            <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
            <Input
                type="search"
                placeholder={placeholder}
                className="pl-8"
                value={localValue}
                onChange={handleChange}
                onKeyDown={handleKeyDown}
                onBlur={onSearch} // Trigger search on blur as well
            />
        </div>
    );
}
