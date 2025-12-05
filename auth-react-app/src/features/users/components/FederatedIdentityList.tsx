import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { type FederatedIdentity } from "@/features/users/types/users.types";
import { format } from "date-fns";
import { Link } from "lucide-react";

interface FederatedIdentityListProps {
    identities?: FederatedIdentity[];
}

export function FederatedIdentityList({ identities }: FederatedIdentityListProps) {
    if (!identities || identities.length === 0) {
        return (
            <Card>
                <CardHeader>
                    <CardTitle>Linked Identities</CardTitle>
                    <CardDescription>External accounts linked to this user.</CardDescription>
                </CardHeader>
                <CardContent>
                    <div className="text-sm text-muted-foreground text-center py-4">
                        No linked identities found.
                    </div>
                </CardContent>
            </Card>
        );
    }

    return (
        <Card>
            <CardHeader>
                <CardTitle>Linked Identities</CardTitle>
                <CardDescription>External accounts linked to this user.</CardDescription>
            </CardHeader>
            <CardContent>
                <div className="space-y-4">
                    {identities.map((identity) => (
                        <div key={`${identity.providerId}-${identity.providerUserId}`} className="flex items-center justify-between p-3 border rounded-lg">
                            <div className="flex items-center gap-3">
                                <div className="h-10 w-10 rounded-full bg-accent flex items-center justify-center">
                                    <Link className="h-5 w-5 text-accent-foreground" />
                                </div>
                                <div>
                                    <div className="font-medium">{identity.providerId}</div>
                                    <div className="text-sm text-muted-foreground">{identity.providerUsername || identity.providerUserId}</div>
                                </div>
                            </div>
                            <div className="text-xs text-muted-foreground">
                                Linked {format(new Date(identity.linkedAt), 'PP')}
                            </div>
                        </div>
                    ))}
                </div>
            </CardContent>
        </Card>
    );
}
